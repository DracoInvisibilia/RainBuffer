package Buffers;

import Managers.SensorManager;
import Sensors.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jklei on 5/29/2017.
 */
public class Box implements Buffer {
    double length = 0;
    double width = 0;
    double area = 0;
    SensorManager sManager;

    public Box(int length, int width) {
        this.length = length;
        this.width = width;
        this.area = this.length*this.width;
        this.sManager = new SensorManager();
    }

    public Box(int length, int width, Sensor sensor) {
        this.length = length;
        this.width = width;
        this.area = this.length*this.width;
    }

    public double getArea() {
        return  this.area;
    }

    public double getContent(double height, int accuracy) {
        return Math.round((this.area*height*Math.pow(10,accuracy))/1000)/Math.pow(10,accuracy);
    }

}
