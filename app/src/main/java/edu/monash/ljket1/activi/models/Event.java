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
    public String startDate;
    public String endDate;
    public String host;
    public String category;
    public String image;

    public Event() {

    }

    public Event(String title, String description, String longitude, String latitude, String startDate, String endDate, String host, String category, String image) {
        this.title = title;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.host = host;
        this.category = category;
        this.image = image;
    }
}
