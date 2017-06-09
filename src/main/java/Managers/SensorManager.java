package Managers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class SensorManager {
    private Map<String, Integer> allSensors;
    private Calendar cal;
    private Date nextUpdate;
    private int updateFrequency = 1;
    private ConnectionManager cManager;

    public SensorManager(ConnectionManager cManager, int updateFrequency) {
        this.updateFrequency = updateFrequency;
        nextUpdate = Calendar.getInstance().getTime();
        this.cManager = cManager;
        allSensors = new HashMap<String, Integer>();
        allSensors.put("WATERFLOW_IN", 2);
        allSensors.put("WATER_LEVEL", 30);
    }

    public Map<String, Integer> pull() {
        Map<String, Integer> pulledVals = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> sensor : allSensors.entrySet()) {
            String sensorName = sensor.getKey();
            int sensorId = sensor.getValue();
            pulledVals.put(sensorName, cManager.fullCommunication("ARDUINO", sensorId));
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
