package com.example.asus.myparkingapp;

/**
 * Created by Asus on 18-09-2016.
 */
public class Vehicles {
    private String TYPE;
    private String NAME;
    private long id;


    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getNAME() {
        return NAME;
    }

    public String getTYPE() {
        return TYPE;
    }
}
