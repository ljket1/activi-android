package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {
    public String title;
    public String description;
    public String longitude;
    public String latitude;
    public String host;

    public Event() {

    }

    public Event(String title, String description, String longitude, String latitude, String host) {
        this.title = title;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.host = host;
    }
}
