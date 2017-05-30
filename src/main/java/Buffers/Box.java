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
    double height = 0;
    double content = 0;
    SensorManager sManager;

    public Box(int length, int width, int height) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.area = this.length*this.width;
        this.content = this.area*this.height;
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

    public double getContent(double emptyHeight, int accuracy) {
        return this.area*(this.height-emptyHeight);
        //return Math.round(this.area*this.height*Math.pow(10,accuracy))-Math.round((this.area*emptyHeight*Math.pow(10,accuracy))/1000)/Math.pow(10,accuracy);
    }

}
