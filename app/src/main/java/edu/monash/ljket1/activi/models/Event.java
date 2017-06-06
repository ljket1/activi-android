package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

@Parcel
@IgnoreExtraProperties
public class Event {
    public String title;
    public String description;
    public String longitude;
    public String latitude;
    public String startTime;
    public String startDate;
    public String endTime;
    public String endDate;
    public String host;
    public String category;

    public Event() {

    }

    public Event(String title, String description, String longitude, String latitude, String startDate, String startTime, String endDate, String endTime, String host, String category) {
        this.title = title;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.host = host;
        this.category = category;
    }
}
