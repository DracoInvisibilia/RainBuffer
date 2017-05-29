package Weather;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class Buienradar implements Weather {
    private double lat = -1;
    private double lon = -1;

    public Buienradar(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Map<String, Double> getUpdate() {
        Map<String, Double> predictionData;
        String update = getRaw();
        predictionData = parseStoStd(update);
        return predictionData;
    }

    public String getRaw() {
        String update = "";
        try {
            String bRadarString = "http://gpsgadget.buienradar.nl/data/raintext?lat=" + lat + "&lon=" + lon;
            URL bRadarGPS = new URL(bRadarString);
            update = Jsoup.parse(bRadarGPS,1000).body().text();
            parseStoStd(update);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return update;
    }


    private Map<String, Double> parseStoStd(String update) {
        Map<String, Double> predictionData = new HashMap<String, Double>();
        String[] elements = update.split(" ");
        for (String element : elements) {
            String[] prediction = element.split("\\|");
            predictionData.put(prediction[1], calculatePrecipitation(Integer.parseInt(prediction[0])));
        }
        return predictionData;
    }

    private double calculatePrecipitation(int value) {
        return Math.pow(10, (value-109)/32);
    }

    public String getName() {
        return "Buienradar";
    }
}
