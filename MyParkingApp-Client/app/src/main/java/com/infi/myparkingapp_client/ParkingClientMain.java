package com.infi.myparkingapp_client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by INFIi on 9/22/2016.
 */
public class ParkingClientMain extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_main);

        findViewById(R.id.scheduler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkingClientMain.this,MainActivity.class));
            }
        });

        findViewById(R.id.get_parking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkingClientMain.this,WiFiDirectActivity.class));
            }
        });
    }
}
