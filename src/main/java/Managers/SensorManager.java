package Managers;

import Sensors.Sensor;
import Sensors.UltraSonic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class SensorManager {
    private List<Sensor> allSensors;

    public SensorManager() {
        allSensors = new ArrayList<Sensor>();
        allSensors.add(new UltraSonic("WATER_LEVEL (in L)"));
    }

    public Map<String, Double> pull() {
        Map<String, Double> pulledVals = new HashMap<String, Double>();
        for (Sensor cSensor: allSensors) {
            pulledVals.put(cSensor.getName(), cSensor.getSingleReading());
        }
        return pulledVals;
    }
}
