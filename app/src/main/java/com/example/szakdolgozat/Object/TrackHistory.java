package com.example.szakdolgozat.Object;

public class TrackHistory {
    private Long date;

    private Double distance;

    public TrackHistory(Long date, Double distance) {
        this.date = date;
        this.distance = distance;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
