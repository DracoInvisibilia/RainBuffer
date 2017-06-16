package Connections;

import Event.EventType;
import Event.Priority;
import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by Dennis on 13-6-2017.
 */
public class RestApi implements ExternalConnection {
    private int updateInterval, id;
    private URL waterLevelUrl, errorUrl;
    private DateTime nextUpdate;

    private void updateNextTime() {
        this.nextUpdate = DateTime.now().plusMinutes(updateInterval);
    }

    private String waterLevelString = "{\"buffer_information\": { \"datetime\": {\n" +
            "    \"date\": {\n" +
            "        \"year\": %1tY,\n" +
            "        \"month\": %1tm,\n" +
            "        \"day\": %1te\n" +
            "    },\n" +
            "    \"time\": {\n" +
            "        \"hour\": %1tH,\n" +
            "        \"minute\": %1tM\n" +
            "    }\n" +
            "}, \"buffer\" : \"%2d\" , \"waterLevel\": \"%2f\"}\n" +
            "}";

    public RestApi(int id, String url,  int updateInterval) {
        this.updateInterval = updateInterval;
        this.id = id;
        this.updateNextTime();
        try {
            this.waterLevelUrl = new URL(url + "/bufferinformations");
            this.errorUrl = new URL(url + "/error");
        } catch (Exception e) {

        }
    }

    @Override
    public void pushError(EventType et, Priority priority, String message) {

    }

    @Override
    public void pushWaterLevel(double d) {
        try {
            HttpURLConnection conn = (HttpURLConnection) waterLevelUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = String.format(waterLevelString, DateTime.now(), id, d);  //"{\"qty\":100,\"name\":\"iPad 4\"}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            conn.disconnect();
            this.updateNextTime();
        } catch (IOException e) {

        }

    }

    @Override
    public Date getNextUpdate() {
        return nextUpdate.toDate();
    }
}
