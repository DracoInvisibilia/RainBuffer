package Managers;

import Event.*;

import java.util.*;

/**
 * Created by jklei on 6/13/2017.
 */
public class EventManager {
    private List<Event> allEvents;

    public EventManager() {
        allEvents = new ArrayList<Event>();
        allEvents.add(createEvent(Priority.NOTIFICATION, EventType.INITIALIZATION, "EventManager was created."));
    }

    public Event createEvent(Priority priority, EventType eventType, String message) {
        Date now = Calendar.getInstance().getTime();
        Event newEvent = new Event(priority, eventType, now, message);
        allEvents.add(newEvent);
        return newEvent;
    }

    public ArrayList<Event> filterEvents(Priority priority, EventType eventType) {
        ArrayList<Event> filteredEvent = new ArrayList<Event>();
        for(Event event : allEvents) {
            if(eventType==event.getEventType() && priority==event.getPriority()) filteredEvent.add(event);
        }
        return filteredEvent;
    }

    public ArrayList<Event> filterEvents(Priority priority) {
        ArrayList<Event> filteredEvent = new ArrayList<Event>();
        for(Event event : allEvents) {
            if(priority==event.getPriority()) filteredEvent.add(event);
        }
        return filteredEvent;
    }

    public ArrayList<Event> filterEvents(EventType eventType) {
        ArrayList<Event> filteredEvent = new ArrayList<Event>();
        for(Event event : allEvents) {
            if(eventType==event.getEventType()) filteredEvent.add(event);
        }
        return filteredEvent;
    }

    public String toString() {
        String writtenEvents = "========== ALL EVENTS: ==========";
        for (Event event : allEvents) {
            writtenEvents += event.toString() + "\n";
        }
        writtenEvents += "=================================\n";
        return writtenEvents;
    }

}
