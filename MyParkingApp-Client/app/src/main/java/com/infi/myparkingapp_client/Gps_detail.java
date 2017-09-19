package com.infi.myparkingapp_client;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by INFIi on 6/12/2016.
 */
public class Gps_detail extends AppCompatActivity {


    String GET_GPS_DATA = "gps_data";
    ArrayList<String> list;
    Intent i;
    String GET_DATA="get_data";
    FrameLayout rootLayout;
    int w,h;
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
            Animation cr= AnimationUtils.loadAnimation(Gps_detail.this,R.anim.activity_open);
            rootLayout.startAnimation(cr);
        }

        i=getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

            //toolbar.setTitle("GPS");

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);



        list=i.getStringArrayListExtra(GET_GPS_DATA);
        final String Title=list.get(0);
        final String Address=list.get(3);
        final String Status=list.get(4);
        final String Radius=list.get(5);
        String Mode=" ";

        char[] t1=list.get(7).toCharArray();
        if(t1[0]=='1')Mode+="Silent";
        if(t1[1]=='1')Mode+="Normal";
        if(t1[2]=='1')Mode+="Wifi On";
        if(t1[3]=='1')Mode+="Wifi Off";

        final CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(Title);

        TextView textView=(TextView)findViewById(R.id.editText);
        textView.setVisibility(View.VISIBLE);
        textView.setTextSize(24);
        textView.setText(Address);

        TextView label1,label2,label3,label4,address,title,radius,status;
        label1=(TextView)findViewById(R.id.label1_cv);label1.setText("Title");
        label2=(TextView)findViewById(R.id.label2_cv);label2.setText("Mode");
        label3=(TextView)findViewById(R.id.label3_cv);label3.setText("Radius");
        label4=(TextView)findViewById(R.id.label4_cv);label4.setText("Status");

        title=(TextView)findViewById(R.id.start_cv);
        address=(TextView)findViewById(R.id.stop_cv);
        radius=(TextView)findViewById(R.id.day_cv);
        status =(TextView)findViewById(R.id.status_cv);
        title.setText(Title);
        address.setText(Mode);
        radius.setText(Radius);
        status.setText(Status);

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context=v.getContext();
                Intent i=new Intent(context,gps.class);
                final int x=(int)v.getX();final int y=(int)v.getY();
                i.putExtra("getX",x);
                i.putExtra("getY",y);
                i.putStringArrayListExtra("data",list);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        });
    }
}
