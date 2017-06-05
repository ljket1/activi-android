package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

@Parcel
@IgnoreExtraProperties
public class Notification {
    public String userId;
    public String userName;
    public String eventTitle;

    public Notification(){
    }

    public Notification(String userId, String userName, String eventTitle) {
        this.userId = userId;
        this.userName = userName;
        this.eventTitle = eventTitle;
    }

}
