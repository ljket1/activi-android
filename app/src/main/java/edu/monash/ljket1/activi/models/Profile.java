package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

@Parcel
@IgnoreExtraProperties
public class Profile {
    public String name;
    public String email;
    public String phone;
    public String image;

    public Profile() {

    }

    public Profile(String name, String email, String phone, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }
}
