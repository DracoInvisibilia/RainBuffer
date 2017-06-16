package Managers;

import Connections.Arduino;
import Connections.Connection;
import Connections.Packets.ArduinoPacket;
import Connections.Packets.Command;
import Connections.Packets.Error;
import Event.EventType;
import Event.Priority;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class ConnectionManager {
    Map<String, Connection> allConnections;
    EventManager eManager;

    public ConnectionManager(boolean init, EventManager eManager) {
        this.eManager = eManager;
        allConnections = new HashMap<String, Connection>();
        allConnections.put("ARDUINO", new Arduino(5, init));
    }

    public int fullCommunication(String name, int command) {
        int response = allConnections.get(name).writeAndRead(command);
        return response;
    }

    public int verifiedCommunication(String name, int sensor, int command, int val) {
        ArduinoPacket transmitPkt = new ArduinoPacket(sensor, command, val);
        allConnections.get(name).sendPacket(transmitPkt);
        ArduinoPacket responsePkt = allConnections.get(name).receivePacket();
        //System.out.println(responsePkt.toString());
        if(responsePkt.isAnswerTo(transmitPkt) && !responsePkt.hasError()) {
            System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.SENSOR_SUCCESS, "Got answer to question: SID=" + sensor + ",COMMAND: " + Command.getCommand(command) + ", namely: " + responsePkt.getValue()).toString());
        } else if (responsePkt.hasError()) {
            System.out.println(eManager.createEvent(Priority.WARNING, EventType.SENSOR_FAILURE, "Got error to question: SID=" + sensor + ",COMMAND: " + Command.getCommand(command) + ", namely: " + Error.getError(responsePkt.getError()).toString()));
             }
        return responsePkt.getValue();
    }

    public Connection getConnection(String name) {
        return allConnections.get(name);
    }
}
