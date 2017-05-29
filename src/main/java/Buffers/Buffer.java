package Buffers;

import Sensors.Sensor;

import java.util.List;

/**
 * Created by jklei on 5/29/2017.
 */
public interface Buffer {
    double getArea();
    double getContent(double height, int accuracy);
}
