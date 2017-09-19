package com.infi.myparkingapp_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



/**
 * Created by INFIi on 6/23/2016.
 */
public class GpsBroadcastReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
            Intent service = new Intent(context, GpsService.class);
            //Toast.makeText(context, "GpsBroadcastReciever", Toast.LENGTH_SHORT).show();
            context.startService(service);

    }
}
