package Connections;

import Connections.Packets.ArduinoPacket;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.Random;

/**
 * Created by jklei on 6/4/2017.
 */
public class Arduino implements HardwareConnection {
    I2CBus ardBus;
    I2CDevice ardDev;
    long wait = 20;

    public Arduino(long wait, boolean init) {
        System.out.println("Starting connection to Arduino...");
        this.wait = wait*1000;
        try {
            System.out.println("Creating bus for Arduino communication...");
            ardBus = I2CFactory.getInstance(I2CBus.BUS_1);
            System.out.println("Creating device for Arduino communication...");
            ardDev = ardBus.getDevice(0x04);
            if(init) initialise(100);
        } catch (I2CFactory.UnsupportedBusNumberException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean initialise(int attempts) {
        try {
            for(int i = 1; i <= attempts; i++) {
                System.out.println("Attempt: " + i + "/" + attempts);

                Random rand = new Random();

                int handshake = rand.nextInt(100);
                System.out.println("Handshake = " + handshake);
                sendPacket(new ArduinoPacket(0,0,handshake));
                System.out.println("Waiting for " + (wait/1000) + "s");
                System.out.println("Reading...");
                int response = -1;
                while (response!=handshake) {
                    ArduinoPacket ap = receivePacket();
                    response = ap.getValue();
                    System.out.println("response: "+ ap.toString() );
                    System.out.println("Handshake vs. Response=" + handshake + " vs. " + response);
                }
                System.out.println("Communication established between Pi and Arduino!");
                return true;
            }
        } finally {
            return false;
        }
    }

    public void write(int request) {
        try {
            ardDev.write((byte) request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] request) {
        try {
            ardDev.write(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(ArduinoPacket ardPkt) {
        byte[] packet = new byte[6];
        packet[0] = (byte) ardPkt.getSensorId();
        packet[1] = (byte) ardPkt.getCommand();
        packet[2] = (byte) ((ardPkt.getValue() >> 24) & 0xFF);
        packet[3] = (byte) ((ardPkt.getValue() >> 16) & 0xFF);
        packet[4] = (byte) ((ardPkt.getValue() >> 8) & 0xFF);
        packet[5] = (byte) ((ardPkt.getValue() >> 0) & 0xFF);
        try {
            ardDev.write(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArduinoPacket receivePacket() {
        ArduinoPacket responsePkt = new ArduinoPacket();
        byte[] response_b = new byte[10];
        try {
            ardDev.read(response_b, 0, 10);
            responsePkt.setSensorId(response_b[0]);
            responsePkt.setCommand(response_b[1]);
            responsePkt.setValue(response_b[2] << 24 | (response_b[3] & 0xFF) << 16 | (response_b[4] & 0xFF) << 8 | (response_b[5] & 0xFF));
            responsePkt.setError(response_b[6] << 24 | (response_b[7] & 0xFF) << 16 | (response_b[8] & 0xFF) << 8 | (response_b[9] & 0xFF));
            //System.out.println("Received packet: " + responsePkt.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responsePkt;
    }



    public int read() {
        int response = -1;
        byte[] response_b = new byte[4];
        try {
            ardDev.read(response_b, 0, 4);
            response = response_b[0] << 24 | (response_b[1] & 0xFF) << 16 | (response_b[2] & 0xFF) << 8 | (response_b[3] & 0xFF);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return response;
        }
    }

    public int writeAndRead(int command) {
        int response = -1;
        write(command);
        while(response==-1) {
            response = read();
        }
        return response;
    }

    public int writeAndRead(byte sensorId, byte command, int val) {
        return 0;
    }

}
