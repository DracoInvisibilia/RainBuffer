import Buffers.Barrel;
import Buffers.Buffer;
import Connections.Packets.ArduinoPacket;
import Event.EventType;
import Event.Priority;
import Managers.*;
import Weather.Weather;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jklei on 5/29/2017.
 */
public class SmartBuffer {
    private String name = null;
    private Buffer buffer;
    private SensorManager sManager;
    private WeatherManager wManager;
    private ConnectionManager cManager;
    private ActuatorManager aManager;
    private EventManager eManager;
    double dischargeRate = 3;
    private Calendar cal;
    private double lat;
    private double lon;
    private int roofWidth;
    private int roofLength;
    private double estimatedFill;
    Map<String, Integer> sensorData;
    private Calendar nextDischargeStart;
    private double nextDischargeLiters;
    private boolean isEmptying;


    public SmartBuffer(String name, double lat, double lon, int roofWidth, int roofLength) {
        System.out.println("Creating Smart Buffer...");
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.roofWidth = roofWidth;
        this.roofLength = roofLength;
        sensorData = new HashMap<String, Integer>();
        this.isEmptying = false;
    }

    public void initialize() {
        eManager = new EventManager();
        eManager.createEvent(Priority.NOTIFICATION, EventType.INITIALIZATION, "Initializing Smart Buffer at GPS location " + this.lat + ", " + this.lon + "...");
        //System.out.println("Initializing Smart Buffer at GPS location " + this.lat + ", " + this.lon + "...");
        buffer = new Barrel(57,83, this.roofWidth*this.roofLength);
        //System.out.println("Buffer type: " + buffer.getType());
        //System.out.println("Roof size: " + buffer.getTargetArea()/10000 + "m2 (" + this.roofWidth + "m by " + this.roofLength + "m)");
        wManager = new WeatherManager(30, this.lat, this.lon);
        cManager = new ConnectionManager(true, eManager);
        sManager = new SensorManager(cManager, 1);
        aManager = new ActuatorManager(cManager);
    }

    public void startSmartness() {
        System.out.println("Starting the smartness...");
        try {
            while(true) { // Smart loop
                cal = Calendar.getInstance();

                //if(sManager!=null) System.out.println("Next update (sensor): " + sManager.getNextUpdate().toString());
                if(sManager!=null && cal.getTime().after(sManager.getNextUpdate())) {
                    System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.UPDATE_REQUEST, "Pulling Sensor Manager...").toString());
                    //System.out.println("Pull \"Sensor Manager\"");
                    Map<String, Integer> sVals = sManager.pull();
                    /*
                    for (Map.Entry<String, Integer> entry : sVals.entrySet()) {
                        String eName = entry.getKey();
                        double eVal = entry.getValue();
                        System.out.println(eName + ": " + eVal);
                    }
                    */
                    sensorData = sVals;
                }

                //System.out.println("Next update (weather): " + wManager.getNextUpdate().toString());
                if(!isEmptying && wManager!=null && cal.getTime().after(wManager.getNextUpdate())) {
                    System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.UPDATE_REQUEST, "Pulling Weather Manager...").toString());
                    Map<Weather, Map<Date, Double>> wVals = wManager.pull();
                    /*
                    for (Map.Entry<Weather, Map<Date, Double>> entry : wVals.entrySet()) {
                        String eName = entry.getKey().getName();
                        Map<Date, Double> eVal = entry.getValue();
                        System.out.println(eName + ": " + eVal);
                    }
                    */

                    if(wManager.predictPrecipitation(wVals)) {
                        /* SMART SCRIPT */
                        //System.out.println("SMART SCRIPT:");
                        Map<Date, Double> precipitationSmartData = new TreeMap<Date, Double>(wManager.estimatePrecipitationSmart(wVals, buffer.getTargetArea()));
                        Object[] allDates = precipitationSmartData.keySet().toArray();
                        int dateIndex = 0;
                        Date currentTime = (Date) allDates[dateIndex];
                        Date firstRain = (Date) allDates[dateIndex];
                        while(precipitationSmartData.get(firstRain)==0.0) {
                            firstRain = (Date) allDates[dateIndex];
                            dateIndex++;
                        }
                        double extraInBuffer = precipitationSmartData.get(allDates[allDates.length-1]);
                        //System.out.println("Newest time: " + currentTime.toString());
                        //System.out.println("First rain: " + firstRain.toString());
                        //System.out.println("Total new in buffer: " + extraInBuffer);


                        if(buffer.getContent(sensorData.get("WATER_LEVEL"), 2)+extraInBuffer>buffer.getTotal(2)) {
                            int intervalMinutes = Minutes.minutesBetween(new DateTime(currentTime.getTime()), new DateTime(firstRain.getTime())).getMinutes();

                            //System.out.println("Interval between now and rain: " + intervalMinutes);

                            double dischargeLiters = extraInBuffer-extraInBuffer*0.8;
                            // double dischargeLiters = sensorData.get("WATER_LEVEL"), 2)+extraInBuffer-buffer.getTotal(2);

                            int dischargeTime = (int) (dischargeLiters / dischargeRate);

                            //System.out.println("Liters to be discharged: " + dischargeLiters);
                            //System.out.println("Discharge time: " + dischargeTime);

                            if (nextDischargeStart == null) {
                                int randomDischarge = (intervalMinutes > dischargeTime) ? new Random().nextInt(intervalMinutes - dischargeTime + 1) : 0;
                                Calendar startDischarge = Calendar.getInstance();
                                startDischarge.setTime(currentTime);
                                startDischarge.add(Calendar.MINUTE, randomDischarge);
                                nextDischargeStart = startDischarge;
                                nextDischargeLiters = dischargeLiters;
                                System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.DISCHARGE, "New discharge scheduled for " + startDischarge.getTime().toString() + " for " + (int)dischargeLiters + "L. (Estimated time: " + dischargeTime + " min)").toString());
                            } else if (dischargeLiters > nextDischargeLiters) {
                                Calendar nextDischargeEnd = (Calendar) nextDischargeStart.clone();
                                nextDischargeEnd.add(Calendar.MINUTE, dischargeTime);
                                while (nextDischargeEnd.after(firstRain) && nextDischargeStart.after(cal.getTime())) {
                                    nextDischargeStart.add(Calendar.MINUTE, -5);
                                    nextDischargeEnd.add(Calendar.MINUTE, -5);
                                }
                                System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.DISCHARGE, "Updated discharge scheduled for " + nextDischargeStart.getTime().toString() + " for " + (int)dischargeLiters + "L. (Estimated time: " + dischargeTime + " min)").toString());
                            } else {
                                System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.DISCHARGE, "Scheduled discharge at " + nextDischargeStart.getTime().toString() + " for " + (int)dischargeLiters + "L. (Estimated time: " + dischargeTime + " min)").toString());
                            }
                        }

                        /* DUMB SCRIPT */
                        /*
                        System.out.println("DUMB SCRIPT: ");
                        double precipitation = wManager.estimatePrecipitation(wVals);
                        double precipitationContent = buffer.getTargetArea()*precipitation/10.0;
                        System.out.println("Estimated precipitation  in the next 2 hours: " + precipitation + "mm");
                        System.out.println("Estimated total from roof: " + precipitationContent + "cm3");
                        System.out.println("Estimated extra liters: " + precipitationContent/1000);
                        //estimatedFill = 150;
                        estimatedFill = precipitationContent/1000;
                        */

                    } else {
                        System.out.println(eManager.createEvent(Priority.NOTIFICATION, EventType.DISCHARGE, "Cancelled discharge scheduled for " + nextDischargeStart.getTime().toString() + " for " + (int)nextDischargeLiters + "L.").toString());
                        nextDischargeStart = null;
                        nextDischargeLiters = 0;
                    }
                }

                if(nextDischargeStart!=null && cal.getTime().after(nextDischargeStart.getTime())) {
                    if(!isEmptying) {
                        System.out.println("EMPYTING: Start emptying.");
                        aManager.update("VALVE_SEWER", true);
                        isEmptying=true;
                    } else {
                        if(buffer.getTotal(2)-nextDischargeLiters >= buffer.getContent(sensorData.get("WATER_LEVEL"), 2)) {
                            System.out.println("EMPTYING: Stop emptying.");
                            aManager.update("VALVE_SEWER", false);
                            isEmptying=false;
                            nextDischargeStart = null;
                            nextDischargeLiters = 0;
                        }
                    }
                }
                /*
                if(estimatedFill > buffer.getEmpty(sensorData.get("WATER_LEVEL"), 2)) {
                    System.out.println("OPEN VALVE! :(");
                    aManager.update("VALVE_GARDEN", true);
                } else {
                    System.out.println("CLOSE VALVE! :)");
                    aManager.update("VALVE_GARDEN", false);
                }
                */
                if(cManager!=null && cal.getTime().after(cManager.getNextUpdate())) {

                    if(sensorData != null){
                        Integer waterLevel = sensorData.get("WATER_LEVEL");
                        if(waterLevel != null){

                            cManager.updateWaterLevel(((double)waterLevel)/1000.0);

                        }


                    }

                }
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
