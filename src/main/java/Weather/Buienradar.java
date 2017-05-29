package Weather;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

    public String getUpdate() {
        String update = "";
        try {
            String bRadarString = "http://gpsgadget.buienradar.nl/data/raintext?lat=" + lat + "&lon=" + lon;
            URL bRadarGPS = new URL(bRadarString);
            update = Jsoup.parse(bRadarGPS,1000).body().text();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return update;
    }

    public String getName() {
        return "Buienradar";
    }
}
