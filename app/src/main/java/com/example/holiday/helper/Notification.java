package com.example.holiday.helper;

public class Notification {
    private String avatar;
    private String creator;
    private String tourName;
    private String type;

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
