package Event;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jklei on 6/13/2017.
 */
public class Event {
    Date eventDate;
    String eventMessage;
    Priority priority;
    EventType eventType;


    public Event(Priority priority, EventType eventType, Date eventDate, String eventMessage) {
        this.eventDate = eventDate;
        this.eventMessage = eventMessage;
        this.priority = priority;
        this.eventType = eventType;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getEventDate() {
        return this.eventDate;
    }

    public String toString() {
        return this.eventDate + " [" + this.priority.getName() + "] " + this.eventType.getName() + ":\t" + this.eventMessage;
    }
}
