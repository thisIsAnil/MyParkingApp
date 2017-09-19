package com.infi.myparkingapp_client;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by Asus on 18-09-2016.
 */
public class AccessGrantedActivity extends Activity {
    SpeedCalculator speedCalculator;
    TextView Distance,Turn;
    int straight,turn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_granted);
        Intent i=getIntent();
        String[] strings=i.getStringExtra("empty_slots").split("//_//");
       if(strings!=null&&strings.length>=7&&!strings[0].equals("-1")){
           GiveDistanceFromEntry distanceFromEntry = new GiveDistanceFromEntry(AccessGrantedActivity.this);
           int straight = distanceFromEntry.getDistance_Straight();
           int empty_slot=Integer.parseInt(strings[0]);
            TextView allocated=(TextView)findViewById(R.id.Allocated_slot_text);
           allocated.setText(empty_slot+"");
           int current=0;
           Distance=(TextView)findViewById(R.id.Distance_Left_text);
           Turn=(TextView)findViewById(R.id.Turn_text);
            speedCalculator=new SpeedCalculator(straight,turn,10,current,true);
           String time=speedCalculator.getTime_Remaining();
           new CountDownTimer(500,speedCalculator.GiveTime(turn-(current-straight))){
               @Override
               public void onTick(long millisUntilFinished) {
                   String time=speedCalculator.getTime_Remaining();
                   String[] s=time.split("//_//");
                    Distance.setText(s[1]);
                   Turn.setText(s[0]);

               }

               @Override
               public void onFinish() {

               }
           }.start();
       }
    }
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
    public class Measures {
        public  int ROAD_WIDTH=10;
        public  int LANE_WIDTH=20;
        public   int LANE_LENGTH=100;
        public  int SLOT_WIDTH=15;
        public Measures(Context context){
            SharedPreferences preferences=context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE);
            IntPref intPref=new IntPref(preferences,KEYS.LANE_WIDTH,LANE_WIDTH);
            LANE_WIDTH=intPref.get();
            intPref=new IntPref(preferences,KEYS.ROAD_WIDTH,ROAD_WIDTH);
            ROAD_WIDTH=intPref.get();
            intPref=new IntPref(preferences,KEYS.SLOT_WIDTH,SLOT_WIDTH);
            SLOT_WIDTH=intPref.get();
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
}
