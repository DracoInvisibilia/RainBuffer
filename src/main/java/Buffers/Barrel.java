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
        System.out.println("emptyHeight: " + emptyHeight);
        System.out.println("Height: " + this.height);
        System.out.println("Area: " + this.area);
        System.out.println("Max Content: " + this.area*this.height);
        System.out.println("Empty content: " + this.area*emptyHeight);
        System.out.println("Height-emptyHeight: " + (this.height-emptyHeight));
        System.out.println("Filled content: " + (this.height-emptyHeight)*this.area);
        return (this.height-emptyHeight);
        //return this.area*(this.height-emptyHeight)/1000;
        //return Math.round(this.area*this.height*Math.pow(10,accuracy))-Math.round((this.area*emptyHeight*Math.pow(10,accuracy))/1000)/Math.pow(10,accuracy);
    }
}
