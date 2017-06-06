package edu.monash.ljket1.activi.models.domain;


import edu.monash.ljket1.activi.models.Notification;

public class NotificationInfo {

    private String key;
    private Notification notification;

    public NotificationInfo(String key, Notification notification) {
        this.key = key;
        this.notification = notification;
    }

    public String getKey() {
        return key;
    }

    public Notification getNotification() {
        return notification;
    }
}
