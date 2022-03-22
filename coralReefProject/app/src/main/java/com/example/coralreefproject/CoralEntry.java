package com.example.coralreefproject;

public class CoralEntry {

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
    String[] images;

    public CoralEntry(String reefName, String coralName, String latitude, String longitude, String airTemp,
                      String waterTemp, String salinity, String cloudCover, String turbidity, String userName, String[] images) {
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
}
