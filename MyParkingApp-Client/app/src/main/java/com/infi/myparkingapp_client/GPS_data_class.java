package com.infi.myparkingapp_client;

/**
 * Created by Infi on 3/24/2016.
 */
public class GPS_data_class {



    private long id;
    private String Title;
    private String latitude;
    private String longitude;
    public String radius;
    private String Address;
    private String mode;
    private int status;


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }


    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getRadius() {
        return radius;
    }


    public long getId() {
        return this.id;
    }

    public int getStatus() {
        return status;
    }

    public String getAddress() {
        return Address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setId(long id) {
        this.id = id;
    }


    public void setAddress(String address) {
        Address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
