package com.example.coralreefproject;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoralEntry implements Serializable {

    String reefName;
    String coralName;
    String latitude;
    String longitude;
    String airTemp;
    String waterTemp;
    String salinity;
    String cloudCover;
    String userName;
    String userId;
    String humidity;
    String windSpeed;
    String windDirection;
    String waveHeight;
    List<String> images;
    String documentId;
    String date;
    String locationAccuracy;
    String waterTurbidity;

    public CoralEntry() {}

    public CoralEntry(String reefName, String coralName, String latitude, String longitude, String airTemp,
                      String waterTemp, String salinity, String cloudCover, String turbidity, String userName,
                      List<String> images, String documentId, String date, String locationAccuracy, String waveHeight,
                      String windDirection, String windSpeed, String humidity, String userId) {
        this.reefName = reefName;
        this.coralName = coralName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.airTemp = airTemp;
        this.waterTemp = waterTemp;
        this.salinity = salinity;
        this.cloudCover = cloudCover;
        this.userName = userName;
        this.userId = userId;
        this.images = images;
        this.documentId = documentId;
        this.locationAccuracy = locationAccuracy;
        this.waterTurbidity = turbidity;
        this.waveHeight = waveHeight;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.date = date;

    }

    public String getReefName() {
        return reefName;
    }

    public void setReefName(String reefName) {
        this.reefName = reefName;
    }

    public String getCoralName() {
        return coralName;
    }

    public void setCoralName(String coralName) {
        this.coralName = coralName;
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

    public String getAirTemp() {
        return airTemp;
    }

    public void setAirTemp(String airTemp) {
        this.airTemp = airTemp;
    }

    public String getWaterTemp() {
        return waterTemp;
    }

    public void setWaterTemp(String waterTemp) {
        this.waterTemp = waterTemp;
    }

    public String getSalinity() {
        return salinity;
    }

    public void setSalinity(String salinity) {
        this.salinity = salinity;
    }

    public String getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(String cloudCover) {
        this.cloudCover = cloudCover;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWaveHeight() {
        return waveHeight;
    }

    public void setWaveHeight(String waveHeight) {
        this.waveHeight = waveHeight;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(String locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public String getWaterTurbidity() {
        return waterTurbidity;
    }

    public void setWaterTurbidity(String waterTurbidity) {
        this.waterTurbidity = waterTurbidity;
    }
}
