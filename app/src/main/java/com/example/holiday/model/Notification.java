package com.example.holiday.model;

public class Notification {
    private final String avatar;
    private final String creator;
    private final String tourName;
    private final String type;

    public Notification(String avatar, String creator, String tourName, String type) {
        this.avatar = avatar;
        this.creator = creator;
        this.tourName = tourName;
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCreator() {
        return creator;
    }

    public String getTourName() {
        return tourName;
    }

    public String getType() {
        return type;
    }
}