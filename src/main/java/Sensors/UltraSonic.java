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
    boolean setup = false;
    String name = null;
    int t_out = 5000;
    int attempts = 10;
    double distance = 0;

    public UltraSonic(String name) {
        System.out.println("Setting up UltraSonic sensor...");
        this.name = name;
        gpio = GpioFactory.getInstance();
        trigPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, PinState.LOW);
        echoPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_09);
        if (!gpio.isShutdown())
            System.out.println("Setup UltraSonic... done.");
        else
            System.out.println("Setup UltraSonic... failed.");
    }

    public double getSingleReading() {
        long pulseStart = 0;
        long pulseEnd = 0;
        long pulseWidth;
        try {
            if (!setup) {
                trigPin.low();
                TimeUnit.MILLISECONDS.sleep(2);
                setup = true;
            }

            trigPin.high();
            TimeUnit.MILLISECONDS.sleep(10);
            trigPin.low();
            long startTime = System.currentTimeMillis();
            boolean timeout = false;
            for (int a = 1; a <= attempts; a++) {
                timeout = false;
                System.out.println("UltraSonic: attempt " + a + " out of " + attempts);
                while (!timeout && echoPin.isLow()) {
                    pulseStart = System.nanoTime();
                    if (System.currentTimeMillis() > startTime + t_out) {
                        System.out.println("Timed out while waiting for echo");
                        timeout = true;
                    }

                }
                startTime = System.currentTimeMillis();
                while (!timeout && echoPin.isHigh()) {
                    pulseEnd = System.nanoTime();
                    if (System.currentTimeMillis() > startTime + t_out) {
                        System.out.println("Timed out while receiving echo");
                        timeout = true;
                    }

                }
            }
            if (timeout) {
                System.out.println("UltraSonic timed out!");
                return distance;
            } else {
                pulseWidth = pulseEnd - pulseStart;
                distance = getDistance(pulseWidth);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            return distance;
        }
    }

    public void getXReading(int X, int interval) {
        long pulseStart = 0;
        long pulseEnd = 0;
        long pulseWidth = 0;
        double distance = 0;
        try {
            if (!setup) {
                trigPin.low();
                TimeUnit.MILLISECONDS.sleep(2);
                setup = true;
            }


            for (int i = 0; i < X; i++) {
                trigPin.high();
                TimeUnit.MILLISECONDS.sleep(10);
                trigPin.low();

                while (echoPin.isLow())
                    pulseStart = System.nanoTime();

                while (echoPin.isHigh())
                    pulseEnd = System.nanoTime();


                pulseWidth = pulseEnd - pulseStart;
                distance = getDistance(pulseWidth);
                TimeUnit.SECONDS.sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getContReading(int interval) {
        long pulseStart = 0;
        long pulseEnd = 0;
        long pulseWidth = 0;
        double distance = 0;
        try {
            if (!setup) {
                trigPin.low();
                TimeUnit.MILLISECONDS.sleep(2);
                setup = true;
            }

            while (true) {
                trigPin.high();
                TimeUnit.MILLISECONDS.sleep(10);
                trigPin.low();

                while (echoPin.isLow())
                    pulseStart = System.nanoTime();

                while (echoPin.isHigh())
                    pulseEnd = System.nanoTime();

                pulseWidth = (pulseEnd - pulseStart);
                distance = getDistance(pulseWidth);
                TimeUnit.SECONDS.sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double getDistance(long pulseWidth) {
        double distance = 1 + ((pulseWidth * 165.7) / 10000000);
        return (distance < 0 || distance > 500) ? -1 : distance;
    }

    public String getName() {
        return this.name;
    }
}
