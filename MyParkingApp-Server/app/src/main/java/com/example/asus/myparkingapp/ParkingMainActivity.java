package com.example.asus.myparkingapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.asus.myparkingapp.FaceVerification.persongroupmanagement.PersonGroupListActivity;
import com.example.asus.myparkingapp.FaceVerification.ui.IdentificationActivity;

/**
 * Created by Asus on 18-09-2016.
 */
public class ParkingMainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_main_activity);
        findViewById(R.id.authenticate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connectivity connectivity=new Connectivity(ParkingMainActivity.this);
                SharedPreferences sharedPreferences=getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE);
                BoolPref boolPref=new BoolPref(sharedPreferences,KEYS.MODE,false);
                boolean IsFingerPrint=boolPref.getBooleanPref();
                if(!IsFingerPrint){
                    if(connectivity.CanConnect()) {
                        startActivity(new Intent(ParkingMainActivity.this, IdentificationActivity.class));
                    }else {
                        startActivity(new Intent(ParkingMainActivity.this,VechileAccessActivity.class));
                    }
                }else {
                    if(Build.VERSION.SDK_INT>=23)startActivity(new Intent(ParkingMainActivity.this,FingerPrintActivity.class));
                    else startActivity(new Intent(ParkingMainActivity.this,VechileAccessActivity.class));
                }
            }
        });
        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkingMainActivity.this,SettingActivity.class));
            }
        });
        findViewById(R.id.extras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkingMainActivity.this,MainActivity.class));
            }
        });
        findViewById(R.id.unallocate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkingMainActivity.this,UnAllocatedSlotActivity.class));
            }
        });

        findViewById(R.id.manage_database).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkingMainActivity.this,PersonGroupListActivity.class));
            }
        });
    }


}
