package Sensors;

/**
 * Created by jklei on 5/29/2017.
 */
public class WaterFlow implements Sensor {
    String name = null;

    public WaterFlow(String name) {
        this.name = name;
    }

    public double getSingleReading() {
        return 0;
    }

    public void getXReading(int X, int interval) {

    }

    public void getContReading(int interval) {

    }

    public String getName() {
        return null;
    }
}
