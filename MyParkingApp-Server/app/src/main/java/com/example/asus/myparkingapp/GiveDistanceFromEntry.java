package com.example.asus.myparkingapp;

import android.content.Context;

/**
 * Created by Asus on 17-09-2016.
 */
public class GiveDistanceFromEntry {
    private int distance_Straight,distance_turn;
    private int lane,slot;
    public GiveDistanceFromEntry(Context context){
        StringPref pref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.LANES,"13");
        int laness=Integer.parseInt(pref.getStringPref());
        pref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.SLOTS_NO,"9");
        int slots=Integer.parseInt(pref.getStringPref());
            this.lane=laness;
        this.slot=slots;
        Measures measures=new Measures(context);
        distance_Straight=lane*measures.LANE_WIDTH+lane*measures.ROAD_WIDTH;
        distance_turn=slot*measures.SLOT_WIDTH;
    }

    public int getDistance_Straight() {
        return distance_Straight;
    }

    public int getDistance_turn() {
        return distance_turn;
    }
}
