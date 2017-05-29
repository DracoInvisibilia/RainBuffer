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
        trigPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, PinState.LOW);
        echoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_09);
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

            trigPin.high();
            TimeUnit.MILLISECONDS.sleep(10);
            trigPin.low();

            while(echoPin.isLow())
                pulseStart = System.currentTimeMillis();

            while(echoPin.isHigh())
                pulseEnd = System.currentTimeMillis();


            pulseWidth = pulseEnd - pulseStart;

            distance = getDistance(pulseWidth);

            System.out.println("Distance: " + distance);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getXReading(int X, int interval) {
        System.out.println("Starting " + X + " readings...");
        long pulseStart = 0;
        long pulseEnd = 0;
        long pulseWidth = 0;
        double distance = 0;
        try {
            System.out.println("Setting up pins and settle sensor...");
            trigPin.low();
            TimeUnit.MILLISECONDS.sleep(2);


            for(int i=0; i < X; i++) {
                trigPin.high();
                TimeUnit.MILLISECONDS.sleep(10);
                trigPin.low();

                while(echoPin.isLow())
                    pulseStart = System.currentTimeMillis();

                while(echoPin.isHigh())
                    pulseEnd = System.currentTimeMillis();


                pulseWidth = pulseEnd - pulseStart;

                distance = getDistance(pulseWidth);

                System.out.println("Distance: " + distance);
                TimeUnit.SECONDS.sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getContReading(int interval) {
        System.out.println("Starting continuous reading...");
        long pulseStart = 0;
        long pulseEnd = 0;
        long pulseWidth = 0;
        double distance = 0;
        try {
            System.out.println("Setting up pins and settle sensor...");
            trigPin.low();
            TimeUnit.MILLISECONDS.sleep(2);

            while(true) {
                trigPin.high();
                TimeUnit.MILLISECONDS.sleep(10);
                trigPin.low();

                while(echoPin.isLow())
                    pulseStart = System.currentTimeMillis();

                while(echoPin.isHigh())
                    pulseEnd = System.currentTimeMillis();

                pulseWidth = (pulseEnd - pulseStart)/1000;

                distance = getDistance(pulseWidth);

                System.out.println("Distance: " + distance);
                TimeUnit.SECONDS.sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getDistance(long pulseWidth) {
        double distance = pulseWidth*165.7;
        return (distance < 0 || distance > 500) ? distance : -1;
    }
}
