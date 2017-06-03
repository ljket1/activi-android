package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

@Parcel
@IgnoreExtraProperties
public class Notifcation {
    public String userId;
    public String eventId;

    public Notifcation(){

    }

    public Notifcation(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

}
