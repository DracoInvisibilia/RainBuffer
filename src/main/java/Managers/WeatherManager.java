package Managers;

import Sensors.Sensor;
import Weather.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by jklei on 5/29/2017.
 */
public class WeatherManager {
    private List<Weather> allWeather;
    private Calendar cal;
    private Date nextUpdate;
    private int updateFrequency = 1;
    private double lat;
    private double lon;

    public WeatherManager(int updateFrequency, double lat, double lon) {
        this.lat = lat;
        this.lon = lon;

        allWeather = new ArrayList<Weather>();
        allWeather.add(new Buienradar(this.lat, this.lon));
        this.updateFrequency = updateFrequency;
        nextUpdate = Calendar.getInstance().getTime();
    }

    public Map<Date, Double> estimatePrecipitationSmart(Map<Weather, Map<Date, Double>> weatherStations, double roofSize) {
        Map<Date, Double> estimatedDownfalls = new TreeMap<Date, Double>();
        Map<Date, Integer> amountOfUpdates = new HashMap<Date, Integer>();
        for(Weather weatherStation : weatherStations.keySet()) {
            Map<Date, Double> wForecasts = weatherStations.get(weatherStation);
            Map<Date, Double> swForecasts = new TreeMap<Date, Double>();
            swForecasts.putAll(wForecasts);
            double totalPrecipitation = 0;
            for(Map.Entry<Date, Double> wForecast : swForecasts.entrySet()) {
                Date currentDate = wForecast.getKey();
                double currentForecast = wForecast.getValue();
                totalPrecipitation += currentForecast*weatherStation.getAvg()/60;
                if(estimatedDownfalls.containsKey(currentDate)) {
                    int updateAmount = amountOfUpdates.get(currentDate);
                    estimatedDownfalls.put(currentDate, roofSize*(estimatedDownfalls.get(currentDate)*((updateAmount)/(updateAmount+1))+(totalPrecipitation*(1/(updateAmount+1))))/10000.0);
                    amountOfUpdates.put(currentDate, updateAmount+1);
                } else {
                    estimatedDownfalls.put(currentDate, roofSize*totalPrecipitation/10000.0);
                    amountOfUpdates.put(currentDate, 1);
                }
            }
        }

        return estimatedDownfalls;
    }

    public double estimatePrecipitation(Map<Weather, Map<Date, Double>> weatherForecasts) {
        double[] precipitations = new double[weatherForecasts.keySet().size()];
        int sCount = 0;
        for (Weather weatherStation : weatherForecasts.keySet()) {
            int t_avg = weatherStation.getAvg();
            double precipitation = 0;
            Map<Date, Double> weatherForecast = weatherForecasts.get(weatherStation);
            for (Double forecast : weatherForecast.values()) {
                precipitation += forecast*t_avg/60.0;
                //System.out.println("Precipitation updates: " + precipitation);
            }
            precipitations[sCount] = precipitation;
        }
        //System.out.println("Precipitation averages: " + calcAvg(precipitations));
        return calcAvg(precipitations);
    }

    public boolean predictPrecipitation(Map<Weather, Map<Date, Double>> weatherForecasts) {
        for (Map.Entry<Weather, Map<Date, Double>> weatherStation : weatherForecasts.entrySet()) {
            Map<Date, Double> forecasts = weatherStation.getValue();
            for (Map.Entry<Date, Double> forecast : forecasts.entrySet()) {
                if (forecast.getValue()>0.001) return true;
            }
        }
        return false;
    }

    public Map<Weather, Map<Date, Double>> pull() {
        Map<Weather, Map<Date, Double>> pulledVals = new HashMap<Weather, Map<Date, Double>>();
        for (Weather cWeather : allWeather) {
            Map<Date, Double> update = cWeather.getUpdate();
            if (update != null) {
                pulledVals.put(cWeather, update);
            }
        }
        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, updateFrequency);
        nextUpdate = cal.getTime();
        return pulledVals;
    }

    private double calcAvg(double[] list) {
        double sum = 0;
        for (int i = 0; i < list.length; i ++) {
            sum += list[i];
        }
        return sum/(double)list.length;
    }

    public Date getNextUpdate() {
        return this.nextUpdate;
    }
}
