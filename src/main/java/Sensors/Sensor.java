package Sensors;

/**
 * Created by Jeroen on 5/28/2017.
 */
public interface Sensor {
    double getSingleReading();
    void getXReading(int X, int interval);
    void getContReading(int interval);
    String getName();
}
