package Weather;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
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

    public Map<Date, Double> getUpdate() {
        Map<Date, Double> predictionData = null;
        String update = getRaw();
        if (!update.equals("")) {
            predictionData = parseStoStd(update);
        }
        return predictionData;
    }

    public String getRaw() {
        String update = "";
        try {
            String bRadarString = "http://gpsgadget.buienradar.nl/data/raintext?lat=" + lat + "&lon=" + lon;
            URL bRadarGPS = new URL(bRadarString);
            update = Jsoup.parse(bRadarGPS, 1000).body().text();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return update;
    }


    private Map<Date, Double> parseStoStd(String update) {
        Calendar now = Calendar.getInstance();
        int[] current_time = {now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)};
        current_time[1] = (current_time[1]+4)/5*5;
        if (current_time[1] >= 60) {
            current_time[1] = current_time[1] - 60;
            current_time[0] = (current_time[1] + 1 >= 24) ? current_time[0] - 24 : current_time[0];
        }

        Map<Date, Double> predictionData = new HashMap<Date, Double>();
        String[] elements = update.split(" ");
        for (String element : elements) {
            String[] prediction = element.split("\\|");
            if(prediction.length < 2) {
                System.out.println("Prediction: " + prediction);
                System.out.println("UpdateStr: " + update);
            }
            String[] time_s = prediction[1].split(":");
            int[] time_i = {Integer.parseInt(time_s[0]), Integer.parseInt(time_s[1])};

            int[] dTime = new int[2];
            if(time_i[1]-current_time[1] < 0) {
                dTime[1] = (time_i[1]-current_time[1])+60;
                dTime[0] = (time_i[0]-current_time[0])-1;
            } else {
                dTime[0] = (time_i[0]-current_time[0]);
                dTime[1] = (time_i[1]-current_time[1]);
            }
            dTime[0] = (dTime[0] < 0) ? dTime[0]+24 : dTime[0];

            Calendar future = Calendar.getInstance();
            future.set(Calendar.HOUR_OF_DAY, current_time[0]);
            future.set(Calendar.MINUTE, current_time[1]);
            future.set(Calendar.SECOND, 0);
            future.add(Calendar.HOUR, dTime[0]);
            future.add(Calendar.MINUTE, dTime[1]);
            if (future.after(now)) {
                predictionData.put(future.getTime(), calculatePrecipitation(Integer.parseInt(prediction[0])));
            }
        }
        return predictionData;
    }

    private double calculatePrecipitation(int value) {
        return Math.pow(10, (value - 109) / 32);
    }

    public String getName() {
        return "Buienradar";
    }
}
