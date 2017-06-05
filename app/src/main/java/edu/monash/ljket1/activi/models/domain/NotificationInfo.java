package edu.monash.ljket1.activi.models.domain;


import edu.monash.ljket1.activi.models.Event;
import edu.monash.ljket1.activi.models.Profile;

public class NotificationInfo {

    private String profileKey;
    private String eventKey;
    private Profile profile;
    private Event event;

    public NotificationInfo(String profileKey, Profile profile, String eventKey, Event event) {
        this.profileKey = profileKey;
        this.profile = profile;
        this.eventKey = eventKey;
        this.event = event;
    }

    public String getProfileKey() {
        return profileKey;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getEventKey() {
        return eventKey;
    }

    public Event getEvent() {
        return event;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
