package Connections.Packets;

/**
 * Created by jklei on 6/12/2017.
 */
public class ArduinoPacket {
    private int sensorId = 0;
    private int command = 0;
    private int value = 0;
    private int error = 0;

    public ArduinoPacket() {}

    public ArduinoPacket(int sensorId, int command) {
        this.sensorId = sensorId;
        this.command = command;
    }

    public ArduinoPacket(int sensorId, int command, int value) {
        this.sensorId = sensorId;
        this.command = command;
        this.value = value;
    }

    public ArduinoPacket(int sensorId, int command, int value, int error) {
        this.sensorId = sensorId;
        this.command = command;
        this.value = value;
        this.error = error;
    }

    public boolean isAnswerTo(ArduinoPacket transmitPkt) {
        return transmitPkt.getSensorId()==this.sensorId && transmitPkt.getCommand()==this.command;
    }

    public boolean hasError() {
        return this.error>0;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String toString() {
        return "\n===============ARDUINO PACKET===============\nSensor:\t\t" + this.sensorId + "\nCommand:\t" + Command.getCommand(this.command) + "\nValue:\t\t" + this.value + "\nError:\t\t" + Error.getError(this.error) + "\n=============================================";
    }


}
