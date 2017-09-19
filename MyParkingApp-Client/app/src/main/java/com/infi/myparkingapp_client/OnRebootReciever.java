package com.infi.myparkingapp_client;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by INFIi on 7/1/2016.
 */
public class OnRebootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent timer =new Intent(context,TimerBroadCastReciever.class);
            timer.putExtra("ID",new long[]{0,0,0});
            PendingIntent pendingTimers=PendingIntent.getBroadcast(context,0,timer,0);
            AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pendingTimers);

            Intent gps=new Intent("YouCantSeeMe");
            PendingIntent pendingGps=PendingIntent.getBroadcast(context,0,gps,0);
            AlarmManager manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pendingGps);
        }
    }
}
