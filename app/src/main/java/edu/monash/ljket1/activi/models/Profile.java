package edu.monash.ljket1.activi.models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
@IgnoreExtraProperties
public class Profile {
    public String name;
    public String email;
    public String phone;

    public Profile() {

    }

    public Profile(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
