package com.example.holiday.model;

public class Notification {
    private final int tourId;//mã chuyến
    private final String avatar;//hình chuyến
    private final String creatorId;//mã người tạo
    private final String creatorName;//tên người tạo
    private final String tourName;//tên chuyến
    private final String status;//tình trạng(đang chờ hay đã vào chuyến

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