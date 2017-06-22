package Connections;

import Event.EventType;
import Event.Priority;
import Event.Event;
import java.util.Date;

public interface ExternalConnection {
    void pushEvent(Event e);
    void pushWaterLevel(double d);
    Date getNextUpdate();
    void pushDischarge(Date d, double amount);
    void pushHeartbeat();
    Date getNextHeartbeat();
}
