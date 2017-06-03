package edu.monash.ljket1.activi.models.domain;

import edu.monash.ljket1.activi.models.Event;

public class EventInfo {

    private String key;
    private Event event;

    public EventInfo(String key, Event event) {
        this.key = key;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public String getKey() {
        return key;
    }
}
