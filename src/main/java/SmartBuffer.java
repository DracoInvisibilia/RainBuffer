import Buffers.Barrel;
import Buffers.Buffer;
import Managers.ConnectionManager;
import Managers.SensorManager;
import Managers.WeatherManager;
import Weather.Weather;

import java.util.Calendar;
import java.util.Date;
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
    private Calendar cal;
    private double lat;
    private double lon;
    private int roofWidth;
    private int roofLength;


    public SmartBuffer(String name, double lat, double lon, int roofWidth, int roofLength) {
        System.out.println("Creating Smart Buffer...");
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.roofWidth = roofWidth;
        this.roofLength = roofLength;
    }

    public void initialize() {
        System.out.println("Initializing Smart Buffer at GPS location " + this.lat + ", " + this.lon + "...");
        buffer = new Barrel(10,100, this.roofWidth*this.roofLength);
        System.out.println("Buffer type: " + buffer.getType());
        System.out.println("Roof size: " + buffer.getTargetArea()/10000 + "m2 (" + this.roofWidth + "m by " + this.roofLength + "m)");
        //sManager = new SensorManager();
        wManager = new WeatherManager(5, this.lat, this.lon);
        cManager = new ConnectionManager();
    }

    public void startSmartness() {
        System.out.println("Starting the smartness...");
        try {
            while(true) { // Smart loop
                cal = Calendar.getInstance();
                if(sManager!=null) {
                    Map<String, Double> sVals = sManager.pull();
                    for (Map.Entry<String, Double> entry : sVals.entrySet()) {
                        String eName = entry.getKey();
                        double eVal = entry.getValue();
                        System.out.println(eName + ": " + buffer.getContent(eVal, 0));
                    }
                }

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
                    }
                }

                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
