package com.plexus.model.account;

public class Devices {

    String device_name;
    String device_login_time;
    double device_latitude;
    double device_longitude;
    String device_token;

    public Devices(String device_name, String device_login_time, double device_latitude, double device_longitude, String device_token){
        this.device_name = device_name;
        this.device_login_time = device_login_time;
        this.device_latitude = device_latitude;
        this.device_longitude = device_longitude;
        this.device_token = device_token;
    }

    public Devices(){

    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_login_time() {
        return device_login_time;
    }

    public void setDevice_login_time(String device_login_time) {
        this.device_login_time = device_login_time;
    }

    public double getDevice_latitude() {
        return device_latitude;
    }

    public void setDevice_latitude(double device_latitude) {
        this.device_latitude = device_latitude;
    }

    public double getDevice_longitude() {
        return device_longitude;
    }

    public void setDevice_longitude(double device_longitude) {
        this.device_longitude = device_longitude;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
