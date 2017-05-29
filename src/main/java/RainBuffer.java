import Sensors.UltraSonic;

/**
 * Created by Jeroen on 5/28/2017.
 */
public class RainBuffer {

    public static void main (String[] args) {
        System.out.println("Latest update: 13:10");
        System.out.println("Initialising Rain Buffer");
        UltraSonic usSensor = new UltraSonic();
        usSensor.getContReading(5);
    }
}
