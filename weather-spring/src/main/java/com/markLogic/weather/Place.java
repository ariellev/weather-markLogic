package com.markLogic.weather;

import com.google.gson.annotations.SerializedName;
import com.marklogic.client.pojo.annotation.GeospatialLatitude;
import com.marklogic.client.pojo.annotation.GeospatialLongitude;

/**
 * Created by ariellev on 01.04.16.
 */
public class Place {

    //{"place":"New York City", "state":"New York", "state_code":"NY", "latitude":40.7069, "longitude":-73.6731}

    @SerializedName("place")
    private String place;

    @SerializedName("state")
    private String state;

    @SerializedName("state_code")
    private String stateCode;

    @GeospatialLatitude
    private String latitude;

    @GeospatialLongitude
    private String longitude;

    public Place() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Place{" +
                "place='" + place + '\'' +
                ", state='" + state + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
