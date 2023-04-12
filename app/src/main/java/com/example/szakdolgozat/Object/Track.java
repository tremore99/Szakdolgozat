package com.example.szakdolgozat.Object;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDate;
import java.util.List;

public class Track {
    private String userId;
    private List<LatLng> track;
    private Long date;

    public Track(String userId, List<LatLng> track, Long date) {
        this.userId = userId;
        this.track = track;
        this.date = date;
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
}
