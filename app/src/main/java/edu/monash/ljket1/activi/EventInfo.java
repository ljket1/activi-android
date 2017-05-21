package edu.monash.ljket1.activi;

import edu.monash.ljket1.activi.models.Event;

class EventInfo {

    private String key;
    private Event event;

    EventInfo(String key, Event event) {
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
