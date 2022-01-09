package com.example.permissioncontrolapp;

public class UserDetails {
    String DeviceId;
    String DenialStatistics;
    String MaybeStatistics;
    String permittedSource;
    String timeStamp;

    UserDetails(){

    }

    public void setPermittedSource(String permittedSource) {
        this.permittedSource = permittedSource;
    }

    public String getPermittedSource() {
        return permittedSource;
    }

    public void setDenialStatistics(String denialStatistics) {
        DenialStatistics = denialStatistics;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public void setMaybeStatistics(String maybeStatistics) {
        MaybeStatistics = maybeStatistics;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDenialStatistics() {
        return DenialStatistics;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public String getMaybeStatistics() {
        return MaybeStatistics;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}

