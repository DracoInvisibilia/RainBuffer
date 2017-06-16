package Managers;

import Connections.Arduino;
import Connections.HardwareConnection;
import Connections.ExternalConnection;
import Connections.Packets.ArduinoPacket;
import Connections.Packets.Command;
import Connections.Packets.Error;
import Connections.RestApi;
import Event.EventType;
import Event.Priority;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jklei on 5/29/2017.
 */
public class ConnectionManager {
    Map<String, HardwareConnection> hardwareConnections;
    EventManager eManager;
    Map<String, ExternalConnection> externalConnections;
    Date nextUpdate;

    private void updateTime(Date time) {
        Calendar cal = Calendar.getInstance();
        if (cal.getTime().after(nextUpdate) || time.before(nextUpdate)) {
            nextUpdate = time;
        }
    }

    public ConnectionManager(boolean init, EventManager eManager) {
        this.eManager = eManager;
        hardwareConnections = new HashMap<String, HardwareConnection>();
        hardwareConnections.put("ARDUINO", new Arduino(5, init));
        ExternalConnection localTonnie = new RestApi(1, "http://localhost", 1);
        externalConnections.put("localTonnie", localTonnie);
        nextUpdate = localTonnie.getNextUpdate();

    }

    public int fullCommunication(String name, int command) {
        int response = hardwareConnections.get(name).writeAndRead(command);
        return response;
    }

    public int verifiedCommunication(String name, int sensor, int command, int val) {
        ArduinoPacket transmitPkt = new ArduinoPacket(sensor, command, val);
        ArduinoPacket responsePkt = hardwareConnections.get(name).receivePacket();
        hardwareConnections.get(name).sendPacket(transmitPkt);
        if (responsePkt.isAnswerTo(transmitPkt) && !responsePkt.hasError()) {
            System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.SENSOR_SUCCESS, "Got answer to question: SID=" + sensor + ",COMMAND: " + Command.getCommand(command) + ", namely: " + responsePkt.getValue()).toString());
        } else if (responsePkt.hasError()) {
            System.out.println(eManager.createEvent(Priority.WARNING, EventType.SENSOR_FAILURE, "Got error to question: SID=" + sensor + ",COMMAND: " + Command.getCommand(command) + ", namely: " + Error.getError(responsePkt.getError()).toString()));
        }
        return responsePkt.getValue();
    }

    public void updateWaterLevel(double d) {
        Date nextUpdate = this.nextUpdate;
        for (Map.Entry<String, ExternalConnection> eec :
                externalConnections.entrySet()) {
            ExternalConnection ec = eec.getValue();
            if (ec.getNextUpdate().before(nextUpdate)) {
                ec.pushWaterLevel(d);
                this.updateTime(ec.getNextUpdate());

            }
        }
    }

    public HardwareConnection getConnection(String name) {
        return hardwareConnections.get(name);
    }

    public Date getNextUpdate() {
        return this.nextUpdate;
    }


}
