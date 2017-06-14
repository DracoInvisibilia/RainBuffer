package Connections;

import Connections.Packets.ArduinoPacket;

/**
 * Created by jklei on 6/4/2017.
 */
public interface HardwareConnection {
    void sendPacket(ArduinoPacket ardPkt);
    ArduinoPacket receivePacket();
    void write(int request);
    void write(byte[] request);
    int writeAndRead(int request);
    int read();
}
