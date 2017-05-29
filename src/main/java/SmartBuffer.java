import Buffers.Barrel;
import Buffers.Buffer;
import Managers.SensorManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by jklei on 5/29/2017.
 */
public class SmartBuffer {
    String name = null;
    Buffer buffer;
    SensorManager sManager;


    public SmartBuffer(String name) {
        System.out.println("Creating Smart Buffer...");
        this.name = name;
    }

    public void initialize() {
        System.out.println("Initializing Smart Buffer...");
        buffer = new Barrel(10);
        sManager = new SensorManager();
    }

    public void startSmartness() {
        System.out.println("Starting the smartness...");
        try {
            while(true) { // Smart loop
                Map<String, Double> newVals = sManager.pullSensors();
                for (Map.Entry<String, Double> entry : newVals.entrySet()) {
                    String eName = entry.getKey();
                    double eVal = entry.getValue();
                    System.out.println(eName + ": " + buffer.getContent(eVal,0));
                }

                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
