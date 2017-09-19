package com.infi.myparkingapp_client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by INFIi on 7/5/2016.
 */
public class OnCancelBr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long[] data=intent.getLongArrayExtra("DATA");

        /*AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        else audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);*/
        //NotificationManager notificationManager=(NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        // notificationManager.cancel(1);


        if(data[1]==1){
            AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
           }
        else if(data[2]==1){
            AudioManager audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        if(data[3]==1){
            WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(false);
        }
        else if (data[4]==1){
            WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            if(!wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(true);
        }

        Intent myIntent = new Intent(context, TimerBroadCastReciever.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        myIntent.putExtra("ID", new long[]{0,0,0});
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pendingIntent);


    }
}
