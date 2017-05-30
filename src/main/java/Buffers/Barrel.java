package Buffers;

import Sensors.Sensor;


/**
 * Created by jklei on 5/29/2017.
 */
public class Barrel implements Buffer {
    double radius = 0;
    double area = 0;
    double height = 0;

    public Barrel(double radius, double height) {
        this.radius = radius;
        this.height = height;
        this.area = Math.PI*Math.pow(radius, 2);
    }

    public Barrel(double radius, double height, Sensor sensor) {
        this.radius = radius;
        this.height = height;
        this.area = Math.PI*Math.pow(radius, 2);
    }

    public double getArea() {
        return  this.area;
    }

    public double getContent(double emptyHeight, int accuracy) {
        return Math.round((this.height-emptyHeight)*this.area)/1000*Math.pow(10,accuracy)/Math.pow(10,accuracy);
    }
}
