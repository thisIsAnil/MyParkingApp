package com.example.asus.myparkingapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Asus on 17-09-2016.
 */
public class Measures {
    public  int ROAD_WIDTH=10;
    public  int LANE_WIDTH=20;
    public   int LANE_LENGTH=100;
    public  int SLOT_WIDTH=15;
    public Measures(Context context){
        SharedPreferences preferences=context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE);
        StringPref stringPref=new StringPref(preferences,KEYS.LANE_WIDTH,LANE_WIDTH+"");
        LANE_WIDTH=Integer.parseInt(stringPref.getStringPref());

        stringPref=new StringPref(preferences,KEYS.ROAD_WIDTH,ROAD_WIDTH+"");
        ROAD_WIDTH=Integer.parseInt(stringPref.getStringPref());
        stringPref=new StringPref(preferences,KEYS.SLOT_WIDTH,SLOT_WIDTH+"");
        SLOT_WIDTH=Integer.parseInt(stringPref.getStringPref());
    }

    public int getLaneLength() {
        return LANE_LENGTH;
    }

    public int getLaneWidth() {
        return LANE_WIDTH;
    }

    public int getRoadWidth() {
        return ROAD_WIDTH;
    }

    public int getSlotWidth() {
        return SLOT_WIDTH;
    }
}
