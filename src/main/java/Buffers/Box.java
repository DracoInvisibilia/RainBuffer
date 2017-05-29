package Buffers;

/**
 * Created by jklei on 5/29/2017.
 */
public class Box implements Buffer {
    double length = 0;
    double width = 0;
    double area = 0;

    public Box(int length, int width) {
        this.length = length;
        this.width = width;
        this.area = this.length*this.width;
    }

    public double getArea() {
        return  this.area;
    }

    public double getContent(double height) {
        return this.area*height;
    }

}
