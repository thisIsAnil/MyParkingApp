package com.example.asus.myparkingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Asus on 17-09-2016.
 */
public class Security extends Activity{

private int empty_slot_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        StringPref stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.EMPTY_SLOTS,"400X200");
       String s=stringPref.getStringPref();
        char[] chars=s.toCharArray();
        empty_slot_no=-1;
        for(int i=0;i<chars.length;i++){
                if(chars[i]=='0'){
                    empty_slot_no=i+1;
                    chars[i]='1';
                    break;
                }
        }
        if(empty_slot_no==-1){
            TextView textView=(TextView)findViewById(R.id.parking_msg);
            textView.setText("Parking Full");
        } else {
            if(empty_slot_no==chars.length) {
                Toast.makeText(Security.this,"Last Slot..No More Available Slot",Toast.LENGTH_LONG).show();
            }
            s=new String(chars);
            stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.EMPTY_SLOTS,s);
            stringPref.setStringPref(s);
            stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.ROAD_WIDTH,"10");
            String roadWith=stringPref.getStringPref();
            stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.LANE_WIDTH,"10");
            String LaneWidth=stringPref.getStringPref();
            stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.SLOT_WIDTH,"10");
            String slot_width=stringPref.getStringPref();
            String Ip_Address=new IpUtil(Security.this).NetwordDetect();
            GiveDistanceFromEntry distanceFromEntry = new GiveDistanceFromEntry(Security.this);
            int straight = distanceFromEntry.getDistance_Straight();
            int turn = distanceFromEntry.getDistance_turn();
            stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.SLOTS_NO,"-1");
            String slot_no=stringPref.getStringPref();
            stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.LANES,"-1");
            String no_of_lanes=stringPref.getStringPref();
            Intent intent = new Intent();
            String message=Ip_Address+"//_//"+s+"//_//"+empty_slot_no + "//_//" + slot_no +"//_//"+no_of_lanes+ "//_//" + roadWith + "//_//" + LaneWidth+"//_//"+slot_width;
            //ip+empty+allocated+no_of_sloits_in each lane+no_of_lanes_roadwidth
            intent.putExtra("empty_slot", message);
            setResult(DeviceDetailFragment.CHOOSE_FILE_RESULT_CODE, intent);
            finish();
        }
    }
}
