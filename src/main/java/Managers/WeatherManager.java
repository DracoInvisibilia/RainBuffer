package Managers;

import Sensors.Sensor;
import Weather.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class WeatherManager {
    private List<Weather> allWeather;

    public WeatherManager() {
        allWeather = new ArrayList<Weather>();
        allWeather.add(new Buienradar(53, 6));
    }

    public Map<String, String> pull() {
        Map<String, String> pulledVals = new HashMap<String, String>();
        for (Weather cWeather: allWeather) {
            pulledVals.put(cWeather.getName(), cWeather.getUpdate());
        }
        return pulledVals;
    }
}
