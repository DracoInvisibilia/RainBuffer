package Managers;

import Connections.Arduino;
import Connections.HardwareConnection;
import Connections.ExternalConnection;
import Connections.Packets.ArduinoPacket;
import Connections.Packets.Command;
import Connections.Packets.Error;
import Connections.RestApi;
import Event.Event;
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
        ExternalConnection localTonnie = new RestApi(1, "http://localhost/app_ton.php", 1);
        externalConnections = new HashMap<String, ExternalConnection>();
        externalConnections.put("localTonnie", localTonnie);
        nextUpdate = localTonnie.getNextUpdate();
        eManager.registerConnectionManager(this);
    }

    public int fullCommunication(String name, int command) {
        int response = hardwareConnections.get(name).writeAndRead(command);
        return response;
    }

    public int verifiedCommunication(String name, int sensor, int command, int val) {
        ArduinoPacket transmitPkt = new ArduinoPacket(sensor, command, val);
        hardwareConnections.get(name).sendPacket(transmitPkt);
        ArduinoPacket responsePkt = hardwareConnections.get(name).receivePacket();
        if (responsePkt.isAnswerTo(transmitPkt) && !responsePkt.hasError()) {
            System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.SENSOR_SUCCESS, "Got answer to question: SID=" + sensor + ",COMMAND: " + Command.getCommand(command) + ", namely: " + responsePkt.getValue()).toString());
            //System.out.println(");
        } else if (responsePkt.hasError()) {
            System.out.println(eManager.createEvent(Priority.WARNING, EventType.SENSOR_FAILURE, "Got error to question: SID=" + sensor + ",COMMAND: " + Command.getCommand(command) + ", namely: " + Error.getError(responsePkt.getError()).toString()));
        }
        return responsePkt.getValue();
    }

    public void updateWaterLevel(double d) {
        Date now = DateTime.now().toDate();
        for (Map.Entry<String, ExternalConnection> eec :
                externalConnections.entrySet()) {
            ExternalConnection ec = eec.getValue();
            System.out.println(now.toString());
            System.out.println(ec.getNextUpdate());

            if (ec.getNextUpdate().before(now)) {
                ec.pushWaterLevel(d);
                System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.UPDATE_SUCCESS, "webserver " + eec.getKey() + " updated waterlevel :" + d));
                this.updateTime(ec.getNextUpdate());

            }


        }


    }

    public void pushEvent(Event e){
            ExternalConnection ec = externalConnections.get("localTonnie");
            if(ec != null){
                ec.pushEvent(e);
            }


    }

    public HardwareConnection getConnection(String name) {
        return hardwareConnections.get(name);
    }

    public Date getNextUpdate() {
        return this.nextUpdate;
    }


}
