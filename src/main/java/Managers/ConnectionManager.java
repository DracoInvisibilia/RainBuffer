package Managers;

import Connections.Arduino;
import Connections.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class ConnectionManager {
    Map<String, Connection> allConnections;

    public ConnectionManager() {
        allConnections = new HashMap<String, Connection>();
        allConnections.put("ARDUINO", new Arduino(5));
    }

    public int fullCommunication(String name, int command) {
        int response = allConnections.get(name).writeAndRead(command);
        return response;
    }
}
