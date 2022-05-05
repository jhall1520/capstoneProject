package com.example.coralreefproject;

import java.io.Serializable;
import java.util.List;

public class CoralEntry implements Serializable {

    // all variables for a coral entry
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
    String time;
    String locationAccuracy;
    String waterTurbidity;
    String numCorals;

    /**
     * Empty Constructor
     */
    public CoralEntry() {}

    /**
     * Constructor
     * @param reefName
     * @param coralName
     * @param latitude
     * @param longitude
     * @param airTemp
     * @param waterTemp
     * @param salinity
     * @param cloudCover
     * @param turbidity
     * @param userName
     * @param images
     * @param documentId
     * @param date
     * @param locationAccuracy
     * @param waveHeight
     * @param windDirection
     * @param windSpeed
     * @param humidity
     * @param userId
     * @param time
     * @param numCorals
     */
    public CoralEntry(String reefName, String coralName, String latitude, String longitude, String airTemp,
                      String waterTemp, String salinity, String cloudCover, String turbidity, String userName,
                      List<String> images, String documentId, String date, String locationAccuracy, String waveHeight,
                      String windDirection, String windSpeed, String humidity, String userId, String time, String numCorals) {
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
        this.time = time;
        this.numCorals = numCorals;

    }

    /**
     * Gets the reef Name
     * @return reefName
     */
    public String getReefName() {
        return reefName;
    }

    /**
     * Sets the reef Name
     * @param reefName
     */
    public void setReefName(String reefName) {
        this.reefName = reefName;
    }

    /**
     * Gets the coral Name
     * @return coralName
     */
    public String getCoralName() {
        return coralName;
    }

    /**
     * Sets Coral Name
     * @param coralName
     */
    public void setCoralName(String coralName) {
        this.coralName = coralName;
    }

    /**
     * Gets Latitude
     * @return
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude
     * @param latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Get Longitude
     * @return
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Sets Longitude
     * @param longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets air temperature
     * @return
     */
    public String getAirTemp() {
        return airTemp;
    }

    /**
     * Sets air temperature
     * @param airTemp
     */
    public void setAirTemp(String airTemp) {
        this.airTemp = airTemp;
    }

    /**
     * Gets water temperature
     * @return
     */
    public String getWaterTemp() {
        return waterTemp;
    }

    /**
     * Sets water temperature
     * @param waterTemp
     */
    public void setWaterTemp(String waterTemp) {
        this.waterTemp = waterTemp;
    }

    /**
     * Get Salinity
     * @return
     */
    public String getSalinity() {
        return salinity;
    }

    /**
     * Set Salinity
     * @param salinity
     */
    public void setSalinity(String salinity) {
        this.salinity = salinity;
    }

    /**
     * Get Cloud Covererage
     * @return
     */
    public String getCloudCover() {
        return cloudCover;
    }

    /**
     * Set Cloud Coverage
     * @param cloudCover
     */
    public void setCloudCover(String cloudCover) {
        this.cloudCover = cloudCover;
    }

    /**
     * Get UserName
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set UserName
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get All images
     * @return
     */
    public List<String> getImages() {
        return images;
    }

    /**
     * Set All Images
     * @param images
     */
    public void setImages(List<String> images) {
        this.images = images;
    }

    /**
     * Get DocumentId (in Firebase Database)
     * @return
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Set Document ID (In Firebase Database)
     * @param documentId
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Get Date
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Set Date
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get Wind Speed
     * @return
     */
    public String getWindSpeed() {
        return windSpeed;
    }

    /**
     * Set Wind Speed
     * @param windSpeed
     */
    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * Get Wind Direction
     * @return
     */
    public String getWindDirection() {
        return windDirection;
    }

    /**
     * Set Wind Direction
     * @param windDirection
     */
    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    /**
     * Get Wave Height
     * @return
     */
    public String getWaveHeight() {
        return waveHeight;
    }

    /**
     * Set Wave Height
     * @param waveHeight
     */
    public void setWaveHeight(String waveHeight) {
        this.waveHeight = waveHeight;
    }

    /**
     * Get Humidity
     * @return
     */
    public String getHumidity() {
        return humidity;
    }

    /**
     * Set Humidity
     * @param humidity
     */
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    /**
     * Get Location Accuracy
     * @return
     */
    public String getLocationAccuracy() {
        return locationAccuracy;
    }

    /**
     * Set Location Accuracy
     * @param locationAccuracy
     */
    public void setLocationAccuracy(String locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    /**
     * Gets water turbidity
     * @return
     */
    public String getWaterTurbidity() {
        return waterTurbidity;
    }

    /**
     * Sets water turbidity
     * @param waterTurbidity
     */
    public void setWaterTurbidity(String waterTurbidity) {
        this.waterTurbidity = waterTurbidity;
    }

    /**
     * Gets Time
     * @return
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets Time
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets number of corals
     * @return
     */
    public String getNumCorals() {
        return numCorals;
    }

    /**
     * Sets number of corals
     * @param numCorals
     */
    public void setNumCorals(String numCorals) {
        this.numCorals = numCorals;
    }


}
