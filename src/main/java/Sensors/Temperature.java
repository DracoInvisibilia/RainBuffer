package Sensors;

/**
 * Created by jklei on 5/29/2017.
 */
public class Temperature implements Sensor {
    String name;

    public Temperature(String name) {
        this.name = name;
    }

    public double getReading() {
        return 0;
    }

    public double calcValue() {
        return 0;
    }
}
