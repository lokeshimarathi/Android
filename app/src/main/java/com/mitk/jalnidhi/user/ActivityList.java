package com.mitk.jalnidhi.user;

public class ActivityList {
    private final String activityKey, getLatitude, getLongitude, getStatus, getDate, getTime, getPlaceName;

    public ActivityList(String activityKey, String getLatitude, String getLongitude, String getStatus, String getDate, String getTime, String getPlaceName) {
        this.activityKey = activityKey;
        this.getLatitude = getLatitude;
        this.getLongitude = getLongitude;
        this.getStatus = getStatus;
        this.getDate = getDate;
        this.getTime = getTime;
        this.getPlaceName = getPlaceName;


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

    public String getGetPlaceName(){return getPlaceName;}


}
