package com.example.holiday.model;

public class Notification {
    private final int tourId;
    private final String avatar;
    private final String creatorId;
    private final String creatorName;
    private final String tourName;
    private final String status;

    public Notification(int tourId, String avatar, String creatorId, String creatorName, String tourName, String status) {
        this.tourId = tourId;
        this.avatar = avatar;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.tourName = tourName;
        this.status = status;
    }

    public int getTourId() {
        return tourId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getTourName() {
        return tourName;
    }

    public String getStatus () {
        return status;
    }
}