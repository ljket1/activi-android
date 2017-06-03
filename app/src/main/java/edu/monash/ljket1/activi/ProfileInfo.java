package edu.monash.ljket1.activi;


import edu.monash.ljket1.activi.models.Profile;

public class ProfileInfo {
    private String key;
    private Profile profile;

    ProfileInfo(String key, Profile profile) {
        this.key = key;
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getKey() {
        return key;
    }
}
