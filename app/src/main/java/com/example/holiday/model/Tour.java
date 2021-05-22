package com.example.holiday.model;

public class Tour {
    private String tourName;
    private String type;
    private String status;
    private String during;
    private String image;

    public Tour(String tourName, String type, String status, String during, String image) {
        this.tourName = tourName;
        this.type = type;
        this.status = status;
        this.during = during;
        this.image = image;
    }

    public String getTourName() {
        return tourName;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getDuring() {
        return during;
    }

    public String getImage() {
        return image;
    }
}