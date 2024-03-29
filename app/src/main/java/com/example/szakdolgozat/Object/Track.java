package com.example.szakdolgozat.Object;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDate;
import java.util.List;

public class Track {
    private String userId;
    private List<LatLng> track;
    private Long date;
    private Double distance;
    private Double avgSpeed;
    private Double maxSpeed;
    private String trackCode;

    public Track(String userId, List<LatLng> track, Long date, Double distance, Double avgSpeed, Double maxSpeed, String trackCode) {
        this.userId = userId;
        this.track = track;
        this.date = date;
        this.distance = distance;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.trackCode = trackCode;
    }

    public String getTrackCode() {
        return trackCode;
    }

    public void setTrackCode(String trackCode) {
        this.trackCode = trackCode;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<LatLng> getTrack() {
        return track;
    }

    public void setTrack(List<LatLng> track) {
        this.track = track;
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
