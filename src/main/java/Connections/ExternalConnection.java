package Connections;

import Event.EventType;
import Event.Priority;

import java.util.Date;

public interface ExternalConnection {
    void pushError(EventType et, Priority priority, String message);
    void pushWaterLevel(double d);
    Date getNextUpdate();


}
