package Connections;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.Random;

/**
 * Created by jklei on 6/4/2017.
 */
public class Arduino implements Connection {
    I2CBus ardBus;
    I2CDevice ardDev;
    long wait = 20;

    public Arduino(long wait) {
        System.out.println("Starting connection to Arduino...");
        this.wait = wait*1000;
        try {
            System.out.println("Creating bus for Arduino communication...");
            ardBus = I2CFactory.getInstance(I2CBus.BUS_1);
            System.out.println("Creating device for Arduino communication...");
            ardDev = ardBus.getDevice(0x04);
            initialise(100);
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
                System.out.println("Handshake: " + handshake);
                byte[] handshake_b = new byte[2];
                write(handshake);
                System.out.println("Waiting for " + (wait/1000) + "s");
                System.out.println("Reading...");
                int response = -1;
                while (response!=handshake) {
                    response = read();
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

}
