package Managers;

import Connections.Arduino;
import Connections.Connection;
import Connections.Packet.ArduinoPacket;
import Connections.Packet.Command;
import Connections.Packet.Error;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class ConnectionManager {
    Map<String, Connection> allConnections;

    public ConnectionManager(boolean init) {
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
        if(responsePkt.isAnswerTo(transmitPkt) && !responsePkt.hasError()) {
            System.out.println("Got answer to question: SID=" + sensor + ",COMMAND:" + Command.getCommand(command) + ", namely: " + responsePkt.getValue());
        } else if (responsePkt.hasError()) {
            System.out.println("Got error to question: SID=" + sensor + ",COMMAND:" + Command.getCommand(command) + ", namely: " + Error.getError(responsePkt.getError()).getName());
        }
        return responsePkt.getValue();
    }

    public Connection getConnection(String name) {
        return allConnections.get(name);
    }
}
