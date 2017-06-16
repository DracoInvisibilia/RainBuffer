package Managers;

import Connections.Packets.Command;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by jklei on 5/29/2017.
 */
public class ActuatorManager {
    private Map<String, Integer> allActuator;
    private Map<String, Boolean> actuatorStates; //false=closed, true=open
    private ConnectionManager cManager;

    public ActuatorManager(ConnectionManager cManager) {
        this.cManager = cManager;
        allActuator = new HashMap<String, Integer>();
        allActuator.put("VALVE_GARDEN", 33);
        actuatorStates = new HashMap<String, Boolean>();
        actuatorStates.put("VALVE_GARDEN", false);
    }

    public void update(String valve, boolean open) {
        System.out.println("Current state: " + valve + ": " + actuatorStates.get(valve));
        System.out.println("State: " + valve + ": " + open);
        if(actuatorStates.get(valve)!=open) {
            cManager.verifiedCommunication("ARDUINO", 7, Command.SET.getCode(), (open) ? 1 : 0);
            actuatorStates.put(valve, open);
        }
    }

}
