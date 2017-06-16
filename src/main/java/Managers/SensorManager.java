package Managers;

import Connections.Packets.Command;

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
    private int updateFrequency_default = 1;
    private int updateFrequency = 1;
    private ConnectionManager cManager;

    public SensorManager(ConnectionManager cManager, int updateFrequency) {
        this.updateFrequency = updateFrequency;
        this.updateFrequency_default = updateFrequency;
        nextUpdate = Calendar.getInstance().getTime();
        this.cManager = cManager;
        allSensors = new HashMap<String, Integer>();
        allSensors.put("WATERFLOW_IN", 2);
        allSensors.put("WATER_LEVEL", 1);
    }

    public void setRelativeUpdateFrequency(double relative) {
        this.updateFrequency*=relative;
        System.out.println("Updated frequency: " + this.updateFrequency);
    }

    public void setUpdateFrequency(int newFrequency) {
        this.updateFrequency = newFrequency;
    }

    public void setDefaultUpdateFrequency() {
        this.updateFrequency = this.updateFrequency_default;
    }

    public Map<String, Integer> pull() {
        Map<String, Integer> pulledVals = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> sensor : allSensors.entrySet()) {
            String sensorName = sensor.getKey();
            int sensorId = sensor.getValue();
            pulledVals.put(sensorName, cManager.verifiedCommunication("ARDUINO", sensorId, Command.GET.getCode(), 0));
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
