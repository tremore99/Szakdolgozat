package com.example.szakdolgozat.Object;

public class TrackCode {
    private String userUID;

    private String code;

    public TrackCode(String userUID, String code) {
        this.userUID = userUID;
        this.code = code;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
