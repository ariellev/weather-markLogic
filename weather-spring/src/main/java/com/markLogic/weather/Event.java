package com.markLogic.weather;

import com.google.gson.annotations.SerializedName;
import com.marklogic.client.pojo.annotation.GeospatialLatitude;
import com.marklogic.client.pojo.annotation.GeospatialLongitude;
import com.marklogic.client.pojo.annotation.Id;

/**
 * Created by ariellev on 20.03.16.
 */
public class Event {

    @Id
    private String id;

    @SerializedName("event_type")
    private String eventType;

    private int fatalities;
    private int injuries;

    @SerializedName("prop_dmg")
    private int propDmg;

    @SerializedName("prop_dmg_exp")
    private int propDmgExp;

    @SerializedName("crop_dmg")
    private int cropDmg;

    @SerializedName("crop_dmg_exp")
    private int cropDmgExp;

    private String remarks;

    @GeospatialLatitude
    private String latitude;

    @GeospatialLongitude
    private String longitude;

    private String date;

    @SerializedName("epoch_time")
    private Long epochTime;

    private String time;

    private String state;
    private Integer year;

    public Event() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getFatalities() {
        return fatalities;
    }

    public void setFatalities(int fatalities) {
        this.fatalities = fatalities;
    }

    public int getInjuries() {
        return injuries;
    }

    public void setInjuries(int injuries) {
        this.injuries = injuries;
    }

    public int getPropDmg() {
        return propDmg;
    }

    public void setPropDmg(int propDmg) {
        this.propDmg = propDmg;
    }

    public int getPropDmgExp() {
        return propDmgExp;
    }

    public void setPropDmgExp(int propDmgExp) {
        this.propDmgExp = propDmgExp;
    }

    public int getCropDmg() {
        return cropDmg;
    }

    public void setCropDmg(int cropDmg) {
        this.cropDmg = cropDmg;
    }

    public int getCropDmgExp() {
        return cropDmgExp;
    }

    public void setCropDmgExp(int cropDmgExp) {
        this.cropDmgExp = cropDmgExp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(Long epochTime) {
        this.epochTime = epochTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", eventType='" + eventType + '\'' +
                ", fatalities=" + fatalities +
                ", injuries=" + injuries +
                ", propDmg=" + propDmg +
                ", propDmgExp=" + propDmgExp +
                ", cropDmg=" + cropDmg +
                ", cropDmgExp=" + cropDmgExp +
                ", remarks='" + remarks + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", date='" + date + '\'' +
                ", epochTime=" + epochTime +
                ", time='" + time + '\'' +
                ", state='" + state + '\'' +
                ", year=" + year +
                '}';
    }
}
