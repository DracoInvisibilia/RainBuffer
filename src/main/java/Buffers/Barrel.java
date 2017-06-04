package Buffers;

import Sensors.Sensor;


/**
 * Created by jklei on 5/29/2017.
 */
public class Barrel implements Buffer {
    private double radius = 0;
    private double area = 0;
    private double height = 0;
    private double targetArea = 0;

    public Barrel(double radius, double height, double targetArea) {
        this.radius = radius;
        this.height = height;
        this.targetArea = targetArea*10000;
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

    public double getTargetArea() {
        return this.targetArea;
    }

    public double getContent(double emptyHeight, int accuracy) {
        return Math.round((this.height-emptyHeight)*this.area)/1000*Math.pow(10,accuracy)/Math.pow(10,accuracy);
    }

    public String getType() {
        return "BARREL";
    }
}
