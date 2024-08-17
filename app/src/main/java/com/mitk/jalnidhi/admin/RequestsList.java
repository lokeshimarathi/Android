package com.mitk.jalnidhi.admin;

public class RequestsList {
    private final String activityKey, getLatitude, getLongitude, getStatus, getDate, getTime, getAuthUserId, getName, getProfileImage, getPhoneNumber, gettaluk, getDistrict, getState;

    public RequestsList(String activityKey, String getLatitude, String getLongitude, String getStatus, String getDate, String getTime, String getAuthUserId, String getName, String getProfileImage, String getPhoneNumber, String gettaluk, String getDistrict, String getState) {
        this.activityKey = activityKey;
        this.getLatitude = getLatitude;
        this.getLongitude = getLongitude;
        this.getStatus = getStatus;
        this.getDate = getDate;
        this.getTime = getTime;
        this.getAuthUserId = getAuthUserId;
        this.getName = getName;
        this.getProfileImage = getProfileImage;
        this.getPhoneNumber = getPhoneNumber;
        this.gettaluk = gettaluk;
        this.getDistrict = getDistrict;
        this.getState = getState;
    }

    public String getActivityKey() {
        return activityKey;
    }

    public String getGetLatitude() {
        return getLatitude;
    }

    public String getGetLongitude() {
        return getLongitude;
    }

    public String getGetStatus() {
        return getStatus;
    }

    public String getGetDate() {
        return getDate;
    }

    public String getGetTime() {
        return getTime;
    }

    public String getGetAuthUserId() {
        return getAuthUserId;
    }

    public String getGetName() {
        return getName;
    }

    public String getGetProfileImage() {
        return getProfileImage;
    }

    public String getGetPhoneNumber() {
        return getPhoneNumber;
    }

    public String getGettaluk() {
        return gettaluk;
    }

    public String getGetDistrict() {
        return getDistrict;
    }

    public String getGetState() {
        return getState;
    }
}
