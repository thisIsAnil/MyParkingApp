package com.example.asus.myparkingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by Asus on 18-09-2016.
 */
public class AccessGrantedActivity extends Activity {
    SpeedCalculator speedCalculator;
    TextView Distance,Turn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_granted);
        Intent i=getIntent();
        String[] strings=i.getStringExtra("empty_slots").split("//_//");
       if(strings!=null&&strings.length>=3&&!strings[0].equals("-1")){

           int straight=Integer.parseInt(strings[1]);
           int turn=Integer.parseInt(strings[2]);
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
           };
       }
    }
}
