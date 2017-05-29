package Sensors;
import com.pi4j.io.gpio.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by jklei on 5/28/2017.
 */
public class UltraSonic implements Sensor {
    GpioPinDigitalOutput trigPin;
    GpioPinDigitalInput echoPin;
    GpioController gpio = null;

    public UltraSonic() {
        System.out.println("Setting up UltraSonic sensor...");
        gpio = GpioFactory.getInstance();
        trigPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
        echoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03);
        if(!gpio.isShutdown())
            System.out.println("Setup UltraSonic... done.");
        else
            System.out.println("Setup UltraSonic... failed.");
    }

    public void getSingleReading() {
        System.out.println("Starting single reading...");
        long pulseStart = 0;
        long pulseEnd = 0;
        long pulseWidth = 0;
        double distance = 0;
        try {
            System.out.println("Setting up pins and settle sensor...");
            trigPin.low();
            TimeUnit.MILLISECONDS.sleep(2);

            System.out.println("Setting and settling done! Send pulse... (Millichanged)");
            trigPin.high();
            TimeUnit.MILLISECONDS.sleep(10);
            trigPin.low();

            System.out.println("Sending pulse done! Waiting for response...");
            while(echoPin.isLow())
                pulseStart = System.currentTimeMillis();

            System.out.println("Got response! Measuring pulse width...");
            while(echoPin.isHigh())
                pulseEnd = System.currentTimeMillis();


            System.out.println("Calculating pulse width...");
            pulseWidth = pulseEnd - pulseStart;

            distance = getDistance(pulseWidth);

            System.out.println("Distance: " + distance);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getXReading(int X, int interval) {
        try {
            for(int i=0; i < X; i++) {
                getSingleReading();
                TimeUnit.SECONDS.sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getContReading(int interval) {
        System.out.println("Starting continuous reading...");
        try {
            while(true) {
                getSingleReading();
                TimeUnit.SECONDS.sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getDistance(long pulseWidth) {
        return pulseWidth*165.7;
    }
}
