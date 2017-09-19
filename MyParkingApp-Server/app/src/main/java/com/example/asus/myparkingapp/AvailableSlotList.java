package com.example.asus.myparkingapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Asus on 17-09-2016.
 */
public class AvailableSlotList {
    private Context context;
    public AvailableSlotList(Context context){
        this.context=context;
    }
    public void SetSlotList(int index,char val){

        SharedPreferences preferences=context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE);
        StringPref stringPref=new StringPref(preferences,KEYS.SLOTS,null);
        String list=stringPref.getStringPref();
        if((list.charAt(index)!=val)&&list!=null){
            char[] chars=list.toCharArray();
            chars[index-1]=val;
            list=chars.toString();
            stringPref=new StringPref(preferences,KEYS.SLOTS,null);
            stringPref.setStringPref(list);
        }
    }
    public String GetSlotList(){

        SharedPreferences preferences=context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE);
        StringPref stringPref=new StringPref(preferences,KEYS.SLOTS,null);
        String list=stringPref.getStringPref();
        return list;
    }
}
