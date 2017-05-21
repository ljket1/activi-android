package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

@Parcel
@IgnoreExtraProperties
public class Rating {
    public String rating;
    public String comment;

    public Rating() {

    }

    public Rating(String rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
