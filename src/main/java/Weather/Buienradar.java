package Weather;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by jklei on 5/29/2017.
 */
public class Buienradar implements Weather {
    private double lat = -1;
    private double lon = -1;
    private int t_avg;

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

    private String getRaw() {
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

    private int calcAvg(ArrayList<Integer> interTimes) {
        double sum = 0;
        for (int i = 0; i < interTimes.size(); i ++) {
            sum += interTimes.get(i);
        }
        return (int)Math.round(sum/interTimes.size());
    }


    private Map<Date, Double> parseStoStd(String update) {
        //update = "255|07:20 255|07:25 255|07:30 255|07:35 255|07:40 255|07:45 255|07:50 255|07:55 255|08:00 255|08:05 255|08:10 255|08:15 255|08:20 255|08:25 255|08:30 255|08:35 255|08:40 15|08:45 15|08:50 15|08:55 15|09:00 15|09:05 150|09:10 150|09:15";
        ArrayList<Integer> interTimes = new ArrayList<Integer>();
        Calendar now = Calendar.getInstance();
        int[] current_time = {now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)};
        current_time[1] = (current_time[1]+4)/5*5;
        if (current_time[1] >= 60) {
            current_time[1] = current_time[1] - 60;
            current_time[0] = (current_time[1] + 1 >= 24) ? current_time[0] - 24 : current_time[0];
        }
        now.set(Calendar.HOUR_OF_DAY, current_time[0]);
        now.set(Calendar.MINUTE, current_time[1]);
        DateTime lastDate = new DateTime(now.getTime());

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
                interTimes.add(Minutes.minutesBetween(lastDate, new DateTime(future.getTime())).getMinutes());
                lastDate = new DateTime(future.getTime());
                predictionData.put(future.getTime(), round(calculatePrecipitation(Double.parseDouble(prediction[0])),2));
            }
        }
        t_avg = calcAvg(interTimes);
        return predictionData;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double calculatePrecipitation(double value) {
        return Math.pow(10, (value - 109) / 32);
    }

    public String getName() {
        return "Buienradar";
    }

    public int getAvg() {
        return t_avg;
    }
}
