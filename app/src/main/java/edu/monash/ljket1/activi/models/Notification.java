package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

@Parcel
@IgnoreExtraProperties
public class Notification {
    public String userId;
    public String name;
    public String category;


    public Notification(){
    }

    public Notification(String userId, String name, String category) {
        this.userId = userId;
        this.name = name;
        this.category = category;
    }

}
