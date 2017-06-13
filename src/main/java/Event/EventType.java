package Event;

/**
 * Created by jklei on 6/13/2017.
 */
public enum EventType {
    SENSOR_FAILURE(0, "SENSOR_FAILURE"),    // Thrown from Arduino when a sensor doesn't work
    UPDATE_FAILURE(1, "UPDATE_FAILURE"),         // Thrown when updating data goes wrong
    CONNECTION_FAILURE(2, "CONNECTION_FAILURE"),     // Connection failure (either between Arduino, WiFi, DB or weather stations
    DANGEROUS_SITUATION(3, "DANGEROUS_SITUATION"),  // Dangerous situations (system becoming too hot/cold)
    CRITICAL_SITUATION(4, "CRITICAL_SITUATION"), // Critical situation (overheating/freezing)
    SYSTEM_FAILURE(5, "SYSTEM_FAILURE"),    // General system failure, cannot continue after this failure
    INITIALIZATION(6, "INITIALIZATION"),
    DISCHARGE(7, "DISCHARGE"),
    UPDATE_SUCCESS(8, "UPDATE_SUCCESS"),
    SENSOR_SUCCESS(9, "SENSOR_SUCCESS"),
    UPDATE_REQUEST(10, "UPDATE_REQUEST");

    private int code;
    private String name;
    EventType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    int getInt() {
       return this.code;
    }

    String getName() {
        return this.name;
    }
}
