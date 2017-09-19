package com.infi.myparkingapp_client;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;


import java.util.ArrayList;

public class main_Detail extends AppCompatActivity {

    String GET_DATA="get_data";
    FrameLayout rootLayout;
    ArrayList<String> list;
    Intent i;
    int w,h;
    @Override
    public void onBackPressed() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            CircularReveal cr=new CircularReveal(rootLayout.getRootView());
            cr.CloseCircularReveal();
        }
       else{ Animation cr= AnimationUtils.loadAnimation(main_Detail.this,R.anim.fab_close);
        rootLayout.startAnimation(cr);}
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_detail);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        rootLayout=(FrameLayout)findViewById(R.id.root_layout);
        Intent i=getIntent();
        w=i.getIntExtra("getX",0);h=i.getIntExtra("getY",0);
        h+=256;
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

            if (savedInstanceState == null) {
                CircularReveal cr;
                if(w==0||h==0)cr=new CircularReveal(rootLayout.getRootView());
                else cr=new CircularReveal(rootLayout.getRootView(),w,h);
                cr.StartCircularReveal();

            }
        }else{
           Animation cr= AnimationUtils.loadAnimation(main_Detail.this,R.anim.activity_open);
           rootLayout.startAnimation(cr);
       }

        i=getIntent();

       Toolbar toolbar=(Toolbar)findViewById(R.id.detail_toolbar);
       //toolbar.setTitle("Timer");
        
        //(findViewById(R.id.main_content)).startAnimation(AnimationUtils.loadAnimation(main_Detail.this,R.anim.fab_open));
        setSupportActionBar(toolbar);
        final ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);



                  list = i.getStringArrayListExtra(GET_DATA);

                  //Toast.makeText(getApplicationContext(),"TImer",Toast.LENGTH_SHORT).show();
                  final String Title = list.get(0);
                  final String StartTime = list.get(1);
                  final String StopTime = list.get(2);
                  final String Days = list.get(3);
                  final String Status = list.get(5);
                  final String isDaily = list.get(4);
                    String Mode=" ";
                  char[] t1=list.get(7).toCharArray();
                    if(t1[0]=='1')Mode+="Silent";
                     if(t1[1]=='1')Mode+="Normal";
                    if(t1[2]=='1')Mode+="Wifi On";
                    if(t1[3]=='1')Mode+="Wifi Off";

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                  collapsingToolbarLayout.setTitle(Title);


                  TextView mStart, mStop, mDays, mStatus;


                  mStart = (TextView) findViewById(R.id.start_cv);
                  mStop = (TextView) findViewById(R.id.stop_cv);
                  mStatus = (TextView) findViewById(R.id.status_cv);
                  mDays = (TextView) findViewById(R.id.day_cv);

                  mStart.setText(Mode);
                  mStop.setText("Start Time: "+StartTime+"\nStop Time:"+StopTime);
                  String day = convertDays(Days);

                    if(isDaily=="N")
                     mDays.setText("Once:"+ day);

                    else
                    mDays.setText(""+day);


                  mStatus.setText(Status);

                  final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit);
                  fab.setOnTouchListener(new View.OnTouchListener() {
                      @Override
                      public boolean onTouch(final View v, MotionEvent motionEvent) {
                          Context context = v.getContext();
                          Intent i = new Intent(context, Timer.class);
                          final int x=(int)v.getX();final int y=(int)v.getY();
                          i.putExtra("getX",x);
                          i.putExtra("getY",y);
                          i.putStringArrayListExtra("data", list);
                          i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          context.startActivity(i);
                          return true;
                      }
                  });




    }


    public String convertDays(String days){
        String day;
        day="";
        char[] tmp=days.toCharArray();
        if(tmp[0]=='1'){day+="Sunday,  ";}
        if(tmp[1]=='1'){day+="Monday,  ";}
        if(tmp[2]=='1'){day+="Tuesday,  ";}
        if(tmp[3]=='1'){day+="Wednesday,  ";}
        if(tmp[4]=='1'){day+="Thursday,  ";}
        if(tmp[5]=='1'){day+="Friday,  ";}
        if(tmp[6]=='1'){day+="Saturday.";}
        return day;
    }
}
