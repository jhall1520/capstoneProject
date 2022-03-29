package com.example.coralreefproject;

import java.io.Serializable;

public class CoralEntry implements Serializable {

    String reefName;
    String coralName;
    String latitude;
    String longitude;
    String airTemp;
    String waterTemp;
    String salinity;
    String cloudCover;
    String turbidity;
    String userName;
    String humidity;
    String windSpeed;
    String windDirection;
    String waveHeight;
    String[] images;
    String documentId;
    String date;

    public CoralEntry() {}

    public CoralEntry(String reefName, String coralName, String latitude, String longitude, String airTemp,
                      String waterTemp, String salinity, String cloudCover, String turbidity, String userName,
                      String[] images, String documentId, String date) {
        this.reefName = reefName;
        this.coralName = coralName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.airTemp = airTemp;
        this.waterTemp = waterTemp;
        this.salinity = salinity;
        this.cloudCover = cloudCover;
        this.turbidity = turbidity;
        this.userName = userName;
        this.images = images;
        this.documentId = documentId;
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

    public String getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(String turbidity) {
        this.turbidity = turbidity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
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
}
