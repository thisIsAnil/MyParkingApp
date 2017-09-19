package com.example.asus.myparkingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Asus on 18-09-2016.
 */
public class VechileAccessActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vechile_access);
        final EditText edittext = (EditText)findViewById(R.id.get_vehicle_no);
        findViewById(R.id.done_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Value = edittext.getText().toString();
                if(!Value.equals(null)){
                    char[] v=Value.toCharArray();
                    if(v.length==12|v.length==13||v.length==14)
                    {
                        String Pattern="^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
                        if(Value.matches(Pattern)){
                            VechileData datasource = new VechileData(VechileAccessActivity.this);
                            datasource.open();

                            List<Vehicles> values = datasource.getAllEntry();
                            BoolPref boolPref=new BoolPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.SUCCEED,true);

                            if(values!=null&&values.contains(Value)){
                                boolPref.setBooleanPref(true);
                                startActivity(new Intent(VechileAccessActivity.this,WiFiDirectActivity.class));
                            }else {
                                boolPref=new BoolPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.SUCCEED,true);
                                boolPref.setBooleanPref(false);
                                startActivity(new Intent(VechileAccessActivity.this,WiFiDirectActivity.class));                            }
                        }else{
                            Toast.makeText(VechileAccessActivity.this,"InValid Pattern",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(VechileAccessActivity.this,"Too Long...",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(VechileAccessActivity.this,"Cannot Be Null",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
