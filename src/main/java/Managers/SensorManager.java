package Managers;

import Connections.Packets.Command;

import java.util.*;

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
    Map<String, Integer> pulledVals;

    public SensorManager(ConnectionManager cManager, int updateFrequency) {
        this.updateFrequency = updateFrequency;
        this.updateFrequency_default = updateFrequency;
        nextUpdate = Calendar.getInstance().getTime();
        this.cManager = cManager;
        allSensors = new HashMap<String, Integer>();
        allSensors.put("WATERFLOW_IN", 2);
        allSensors.put("WATER_LEVEL", 1);
        allSensors.put("WATERFLOW_SEWER", 3);
        allSensors.put("WATERFLOW_FAUCET", 4);
        allSensors.put("WATERFLOW_GARDEN", 5);
        pulledVals = new HashMap<String, Integer>();
    }

    public void setRelativeUpdateFrequency(double relative) {
        this.updateFrequency*=relative;
        System.out.println("Updated frequency: " + this.updateFrequency);
    }

    public ArrayList<String> getActiveFlows() {
        String[] possibleSensors = new String[]{"WATERFLOW_SEWER","WATERFLOW_FAUCET","WATERFLOW_GARDEN"};
        ArrayList<String> activeFlows = new ArrayList<String>();
        for (int i = 0; i < possibleSensors.length; i++) {
            if (pulledVals.get(possibleSensors[i]) > 0) activeFlows.add(possibleSensors[i]);
        }
        return activeFlows;
    }

    public void setUpdateFrequency(int newFrequency) {
        this.updateFrequency = newFrequency;
    }

    public void setDefaultUpdateFrequency() {
        this.updateFrequency = this.updateFrequency_default;
    }

    public Map<String, Integer> pull() {
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
