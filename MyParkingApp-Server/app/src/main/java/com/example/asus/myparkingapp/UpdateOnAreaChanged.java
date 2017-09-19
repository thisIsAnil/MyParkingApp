package com.example.asus.myparkingapp;

import android.content.Context;

/**
 * Created by INFIi on 9/19/2016.
 */
public class UpdateOnAreaChanged {

    public UpdateOnAreaChanged(Context context,boolean isChanged,String val){
        StringPref stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.Area,"-1");
        String s1=stringPref.getStringPref();
        if(s1.equals("-1")||isChanged) {
            stringPref.setStringPref(val);
            stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE), KEYS.Area, "400X200");
            String s = stringPref.getStringPref();
            String[] strings = s.split("X");
            int length = Integer.parseInt(strings[0]);
            int width = Integer.parseInt(strings[1]);
            Measures measures=new Measures(context);
            int lanes = length / (measures.ROAD_WIDTH + measures.LANE_WIDTH);
            int slots = (width - (2 * measures.ROAD_WIDTH)) / measures.SLOT_WIDTH;
            stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.LANES, lanes + "");
            stringPref.setStringPref(lanes + "");
            stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.SLOTS_NO, slots + "");
            stringPref.setStringPref(slots + "");
                String emptySlots = "";
                for (int i = 0; i < ((lanes)*(slots - 1)); i++) {
                        emptySlots+="0";
                }
            stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.EMPTY_SLOTS,emptySlots);
                stringPref.setStringPref(emptySlots);

        }
    }
}
