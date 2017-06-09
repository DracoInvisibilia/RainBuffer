import Buffers.Barrel;
import Buffers.Buffer;
import Managers.ActuatorManager;
import Managers.ConnectionManager;
import Managers.SensorManager;
import Managers.WeatherManager;
import Weather.Weather;

import java.security.KeyManagementException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    ConnectionManager cManager;
    ActuatorManager aManager;
    private Calendar cal;
    private double lat;
    private double lon;
    private int roofWidth;
    private int roofLength;
    private double estimatedFill;
    Map<String, Integer> sensorData;


    public SmartBuffer(String name, double lat, double lon, int roofWidth, int roofLength) {
        System.out.println("Creating Smart Buffer...");
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.roofWidth = roofWidth;
        this.roofLength = roofLength;
        sensorData = new HashMap<String, Integer>();
    }

    public void initialize() {
        System.out.println("Initializing Smart Buffer at GPS location " + this.lat + ", " + this.lon + "...");
        buffer = new Barrel(57,83, this.roofWidth*this.roofLength);
        System.out.println("Buffer type: " + buffer.getType());
        System.out.println("Roof size: " + buffer.getTargetArea()/10000 + "m2 (" + this.roofWidth + "m by " + this.roofLength + "m)");
        wManager = new WeatherManager(900, this.lat, this.lon);
        cManager = new ConnectionManager();
        sManager = new SensorManager(cManager, 1);
        aManager = new ActuatorManager(cManager);
    }

    public void startSmartness() {
        System.out.println("Starting the smartness...");
        try {
            while(true) { // Smart loop
                cal = Calendar.getInstance();

                System.out.println("Next update (sensor): " + sManager.getNextUpdate().toString());
                if(sManager!=null && cal.getTime().after(sManager.getNextUpdate())) {
                    System.out.println("Pull \"Sensor Manager\"");
                    Map<String, Integer> sVals = sManager.pull();
                    for (Map.Entry<String, Integer> entry : sVals.entrySet()) {
                        String eName = entry.getKey();
                        double eVal = entry.getValue();
                        System.out.println(eName + ": " + eVal);
                    }
                    sensorData = sVals;
                }

                System.out.println("Next update (weather): " + wManager.getNextUpdate().toString());
                if(wManager!=null && cal.getTime().after(wManager.getNextUpdate())) {
                    System.out.println("Pull \"Weather Manager\"");
                    Map<Weather, Map<Date, Double>> wVals = wManager.pull();
                    /*
                    for (Map.Entry<Weather, Map<Date, Double>> entry : wVals.entrySet()) {
                        String eName = entry.getKey().getName();
                        Map<Date, Double> eVal = entry.getValue();
                        System.out.println(eName + ": " + eVal);
                    }
                    */

                    if(wManager.predictPrecipitation(wVals)) {
                        double precipitation = wManager.estimatePrecipitation(wVals);
                        double precipitationContent = buffer.getTargetArea()*precipitation/10.0;
                        System.out.println("Estimated precipitation  in the next 2 hours: " + precipitation + "mm");
                        System.out.println("Estimated total from roof: " + precipitationContent + "cm3");
                        System.out.println("Estimated extra liters: " + precipitationContent/1000);
                        estimatedFill = 150;
                        //estimatedFill = precipitationContent/1000;
                    }
                }

                if(estimatedFill > buffer.getEmpty(sensorData.get("WATER_LEVEL"), 2)) {
                    System.out.println("OPEN VALVE! :(");
                    aManager.update("VALVE_GARDEN", true);
                } else {
                    System.out.println("CLOSE VALVE! :)");
                    aManager.update("VALVE_GARDEN", false);
                }

                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
