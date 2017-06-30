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
    HashMap<String, Integer> allSensors;
    public ConnectionManager(boolean init, EventManager eManager) {
        this.eManager = eManager;
        hardwareConnections = new HashMap<String, HardwareConnection>();
        hardwareConnections.put("ARDUINO", new Arduino(5, init));
        ExternalConnection localTonnie = new RestApi(1, "http://localhost/app_ton.php", 1,1);
        ExternalConnection theGreatServer = new RestApi(1, "http://regenbuffer.student.utwente.nl/app.php", 1,1);
        externalConnections = new HashMap<String, ExternalConnection>();
      //  externalConnections.put("localTonnie", localTonnie);
        externalConnections.put("theGreatServer",theGreatServer);
        nextUpdate = localTonnie.getNextUpdate();
        eManager.registerConnectionManager(this);
        allSensors = new HashMap<String, Integer>();
        allSensors.put("WATERFLOW_IN", 2);
        allSensors.put("WATER_LEVEL", 1);
        allSensors.put("WATERFLOW_SEWER", 3);
        allSensors.put("WATERFLOW_FAUCET", 4);
        allSensors.put("WATERFLOW_GARDEN", 5);



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

    public void updateWaterLevel(double d, HashMap<String, Double> waterFlowsOut) {
        Date now = DateTime.now().toDate();

        for (Map.Entry<String, ExternalConnection> eec :
                externalConnections.entrySet()) {
            ExternalConnection ec = eec.getValue();

            if (ec.getNextUpdate().before(now)) {
                ec.pushWaterLevel(d);
                System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.UPDATE_SUCCESS, "webserver " + eec.getKey() + " updated waterlevel :" + d));
                this.updateTime(ec.getNextUpdate());
                if(waterFlowsOut != null && !waterFlowsOut.isEmpty()){
                    for (Map.Entry<String,Double> es:waterFlowsOut.entrySet()
                    ){

                        ec.pushWaterFlow(es.getValue(),allSensors.get(es.getKey()));
                    }


                }

            }
            if(ec.getNextHeartbeat().before(now)){
                ec.pushHeartbeat();
                this.updateTime(ec.getNextHeartbeat());
                System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.UPDATE_SUCCESS, "webserver " + eec.getKey() + " updated heartbeat"));

            }
        }
    }

    public void pushEvent(Event e) {
        ExternalConnection ec = externalConnections.get("localTonnie");
        if (ec != null) {
            ec.pushEvent(e);
        }


    }

    public void pushDischarge(Date d, double amount) {
        for (Map.Entry<String, ExternalConnection> eec :
                externalConnections.entrySet()) {
            ExternalConnection ec = eec.getValue();
            ec.pushDischarge(d,amount);
            System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.UPDATE_SUCCESS, "webserver " + eec.getKey() + " updated discharge :" + d));

        }

    }



    public HardwareConnection getConnection(String name) {
        return hardwareConnections.get(name);
    }

    public Date getNextUpdate() {
        return this.nextUpdate;
    }


}
