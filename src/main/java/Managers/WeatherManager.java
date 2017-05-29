package Managers;

import Sensors.Sensor;
import Weather.*;

import java.util.*;

/**
 * Created by jklei on 5/29/2017.
 */
public class WeatherManager {
    private List<Weather> allWeather;
    private Calendar cal;
    private Date nextUpdate;
    private int updateFrequency = 1;

    public WeatherManager(int updateFrequency) {
        allWeather = new ArrayList<Weather>();
        allWeather.add(new Buienradar(53, 6));
        this.updateFrequency = updateFrequency;
        nextUpdate = Calendar.getInstance().getTime();
    }

    public Map<String, Map<String, Double>> pull() {
        Map<String, Map<String, Double>> pulledVals = new HashMap<String, Map<String, Double>>();
        for (Weather cWeather: allWeather) {
            pulledVals.put(cWeather.getName(), cWeather.getUpdate());
        }
        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, updateFrequency);
        nextUpdate = cal.getTime();
        return pulledVals;
    }

    public Date getNextUpdate() {
        return this.nextUpdate;
    }
}
