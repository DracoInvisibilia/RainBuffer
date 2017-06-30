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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Event.Event;

/**
 * Created by Dennis on 13-6-2017.
 */
public class RestApi implements ExternalConnection {
    private int updateInterval, id, heartbeatinterval;
    private URL waterLevelUrl, errorUrl, dischargeUrl, heartbeatUrl;
    private DateTime nextUpdate;
    private DateTime nextHeartbeat;

    private void updateNextTime() {
        this.nextUpdate = DateTime.now().plusMinutes(updateInterval);
    }

    private void updateHeartbeat() {
        this.nextHeartbeat = DateTime.now().plusMinutes(heartbeatinterval);
    }


    private String createDateString(Date dt) {
        return String.format(dateString, dt, new SimpleDateFormat("M").format(dt));
    }


    private String dateString = "{\n" +
            "    \"date\": {\n" +
            "        \"year\": %1$tY,\n" +
            "        \"month\": %2$s,\n" +
            "        \"day\": %1$te\n" +
            "    },\n" +
            "    \"time\": {\n" +
            "        \"hour\": %1$tk,\n" +
            "        \"minute\": %1$tM\n" +
            "    }\n" +
            "}";

    private String eventString = "{\"event\": { \"datetime\":  %1$s, \"buffer\" : \"%2$d\" , \"eventtype\": \"%3$d\", \"priority\" : \"%4$d\", \"message\": \"%5$s\"}\n" +
            "}\n";

    private String waterLevelString = "{\"buffer_information\": { \"datetime\": %1$s, \"buffer\" : \"%2$d\" , \"waterLevel\": \"%3$f\"}\n" +
            "}";

    private String dischargeString = "{\"datetime\": \"%1$s\" , \"amount\" : \"%2$f\"}";

    public RestApi(int id, String url, int updateInterval, int heartbeatInterval) {
        this.updateInterval = updateInterval;
        this.id = id;
        this.nextUpdate = DateTime.now();
        this.nextHeartbeat = DateTime.now();
        this.heartbeatinterval = heartbeatInterval;
        try {
            this.waterLevelUrl = new URL(url + "/bufferinformations");
            this.errorUrl = new URL(url + "/events");
            this.dischargeUrl = new URL(url + "/buffers/1/planneds");
            this.heartbeatUrl = new URL(url + "/buffers/1/heartbeats");
        } catch (Exception e) {

        }
    }

    @Override
    public void pushEvent(Event e) {
        try {
            HttpURLConnection conn = (HttpURLConnection) errorUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String input = String.format(eventString, this.createDateString(e.getEventDate()),
                    id,
                    e.getEventType().getInt(),
                    e.getPriority().getPriority(),
                    e.getEventMessage());

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                System.out.println("Failed : HTTP error code : "
                        + conn.getResponseCode());
                System.out.println("original message\n" + input);

                System.out.println("Return message\n" + conn.getResponseMessage());
                System.out.println("Return message\n" + conn.getContent().toString());

                System.out.println("end");

            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            conn.disconnect();
        } catch (IOException er) {
            er.printStackTrace();
        }

    }


    @Override
    public void pushDischarge(Date d, double amount) {
        try {
            HttpURLConnection conn = (HttpURLConnection) dischargeUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            Date dt = DateTime.now().toDate();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            String input = String.format(dischargeString, df.format(d), amount);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
              /*  System.out.println("Failed : HTTP error code : "
                        + conn.getResponseCode());
                System.out.println("original message\n" + input);

                System.out.println("Return message\n" + conn.getResponseMessage());
                System.out.println("Return message\n" + conn.getContent().toString());

                System.out.println("end");
*/
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            conn.disconnect();
            this.updateNextTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void pushHeartbeat() {
        try {
            HttpURLConnection conn = (HttpURLConnection) heartbeatUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String input = "";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT ||conn.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
  /*              System.out.println("Failed : HTTP error code : "
                        + conn.getResponseCode());
                System.out.println("original message\n" + input);

                System.out.println("Return message\n" + conn.getResponseMessage());
                System.out.println("Return message\n" + conn.getContent().toString());

                System.out.println("end");
*/
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            conn.disconnect();
            this.updateHeartbeat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void pushWaterLevel(double d) {
        try {
            HttpURLConnection conn = (HttpURLConnection) waterLevelUrl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            Date dt = DateTime.now().toDate();
            String input = String.format(waterLevelString, this.createDateString(dt), id, d);  //"{\"qty\":100,\"name\":\"iPad 4\"}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
  /*              System.out.println("Failed : HTTP error code : "
                        + conn.getResponseCode());
                System.out.println("original message\n" + input);

                System.out.println("Return message\n" + conn.getResponseMessage());
                System.out.println("Return message\n" + conn.getContent().toString());

                System.out.println("end");
*/
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            conn.disconnect();
            this.updateNextTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Date getNextHeartbeat() {
        return nextHeartbeat.toDate();
    }

    @Override
    public Date getNextUpdate() {
        return nextUpdate.toDate();
    }
}
