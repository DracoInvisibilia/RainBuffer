/**
 * Created by Jeroen on 5/28/2017.
 */
public class main {

    public static void main (String[] args) {
        //Box coordinates: 52.24, 6.85
        //Test data: 50.5, 5.9c
        SmartBuffer buffer = new SmartBuffer("Bob", 50.5, 5.9, 4, 5);
        buffer.initialize();
        buffer.startSmartness();
    }
}
