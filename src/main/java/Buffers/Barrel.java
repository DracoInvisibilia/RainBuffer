package Buffers;

import Sensors.Sensor;


/**
 * Created by jklei on 5/29/2017.
 */
public class Barrel implements Buffer {
    double radius = 0;
    double area = 0;

    public Barrel(double radius) {
        this.radius = radius;
        this.area = Math.PI*Math.pow(radius, 2);
    }

    public Barrel(double radius, Sensor sensor) {
        this.radius = radius;
        this.area = Math.PI*Math.pow(radius, 2);
    }

    public double getArea() {
        return  this.area;
    }

    public double getContent(double height, int accuracy) {
        return Math.round((this.area*height*Math.pow(10,accuracy))/1000)/Math.pow(10,accuracy);
    }
}
