import Buffers.Barrel;
import Buffers.Buffer;
import Managers.SensorManager;
import Managers.WeatherManager;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by jklei on 5/29/2017.
 */
public class SmartBuffer {
    String name = null;
    Buffer buffer;
    SensorManager sManager;
    WeatherManager wManager;
    private Calendar cal;


    public SmartBuffer(String name) {
        System.out.println("Creating Smart Buffer...");
        this.name = name;
    }

    public void initialize() {
        System.out.println("Initializing Smart Buffer...");
        buffer = new Barrel(10);
        sManager = new SensorManager();
        wManager = new WeatherManager(5);
    }

    public void startSmartness() {
        System.out.println("Starting the smartness...");
        try {
            while(true) { // Smart loop
                cal = Calendar.getInstance();

                Map<String, Double> sVals = sManager.pull();
                for (Map.Entry<String, Double> entry : sVals.entrySet()) {
                    String eName = entry.getKey();
                    double eVal = entry.getValue();
                    System.out.println(eName + ": " + buffer.getContent(eVal,0));
                }

                if(cal.getTime().after(wManager.getNextUpdate())) {
                    Map<String, Map<String, Double>> wVals = wManager.pull();
                    for (Map.Entry<String, Map<String, Double>> entry : wVals.entrySet()) {
                        String eName = entry.getKey();
                        Map<String, Double> eVal = entry.getValue();
                        System.out.println(eName + ": " + eVal.toString());
                    }
                }

                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
