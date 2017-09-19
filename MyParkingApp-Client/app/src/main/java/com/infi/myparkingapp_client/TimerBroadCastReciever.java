package com.infi.myparkingapp_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by INFIi on 6/5/2016.
 */
public class TimerBroadCastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long[] data=intent.getLongArrayExtra("ID");

            Intent serviceIntent = new Intent(context, TimerService.class);
        //Toast.makeText(context,data[0]+" ",Toast.LENGTH_SHORT).show();
            serviceIntent.putExtra("ID",data);
            context.startService(serviceIntent);

    }
}
