package com.infi.myparkingapp_client;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by INFIi on 7/5/2016.
 */
public class SilentIntent extends Service {
    long diff2=0;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    private Notification createNotification( String output) {
        final NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT < 21) {
            Intent cancelIntent=new Intent(this,OnCancelBr.class);
            PendingIntent cancel=PendingIntent.getActivity(this,1,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Silence")
                    .setContentText(output)
                    .setSmallIcon(R.drawable.ic_timer_add)
                    .addAction(R.drawable.ic_highlight_off_white_24dp,"Cancel",cancel)
                    .setShowWhen(true)
                    .setUsesChronometer(true);

            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(this, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            return builder.build();
        } else {

            Resources res = this.getResources();
            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(this, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            Intent cancelIntent=new Intent(this,OnCancelBr.class);
            PendingIntent cancel=PendingIntent.getActivity(this,1,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            final Notification notification = new Notification.Builder(getApplicationContext())
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.drawable.ic_timer_add)
                    .addAction(R.drawable.ic_highlight_off_white_24dp,"Cancel",cancel)
                    .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(0))
                    .setContentTitle("Silence")
                    .setShowWhen(true)
                    .setUsesChronometer(true)
                    .setContentText(output)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(resultPendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_timer_add))
                    .setAutoCancel(true)
                    .build();

            if(notification.actions[0].actionIntent!=null){
                stopForeground(true);
            }
            return notification;
        }
    }
    public void ShowNotification(){
        final AudioManager mode=(AudioManager)getSystemService(AUDIO_SERVICE);
        final Notification notification=createNotification( "In Silent Mode");
        startForeground(1,notification );

        new CountDownTimer(diff2,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(mode.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){
                    stopForeground(true);
                    stopSelf();
                }
            }

            @Override
            public void onFinish() {
                stopForeground(true);
                stopSelf();
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long[] data = intent.getLongArrayExtra("Diff");
        final long tmp = data[0];
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        long cur_time = (cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000) + (cal.get(Calendar.MINUTE) * 60 * 1000) + (cal.get(Calendar.SECOND) * 1000);

        diff2 = tmp - cur_time;

        if (diff2==0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent myIntent = new Intent(getApplicationContext(), TimerBroadCastReciever.class);
            //myIntent.putExtra("alarm", new Timer());
            myIntent.putExtra("ID", new long[]{0,0,0});
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);
            cal.setTimeInMillis(System.currentTimeMillis());
            alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        } else if (diff2 > 0) {
            /*SharedPreferences preferences=getSharedPreferences("Show_Notification",MODE_PRIVATE);
            Show=preferences.getInt("IsShow",0);
            if(Show==1){
                ShowNotification();
            }*/
            AudioManager audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
            if(data[1]==1){
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);}
            else if(data[2]==1){
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
            if(data[3]==1){
                WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                if(!wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(true);
            }
            else if (data[4]==1){
                WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
                if(wifiManager.isWifiEnabled())wifiManager.setWifiEnabled(false);

            }
            Intent i = new Intent(getApplicationContext(), OnCancelBr.class);
            i.putExtra("DATA",data);
            PendingIntent p = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            cal.set(Calendar.MILLISECOND,0);
            cal.set(Calendar.SECOND,0);
            cal.setTimeInMillis(System.currentTimeMillis() + (diff2-1000));

            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), p);
            //Toast.makeText(getApplicationContext(),"In Silent Intent ",Toast.LENGTH_SHORT).show();

            Intent myIntent = new Intent(getApplicationContext(), TimerBroadCastReciever.class);
            myIntent.putExtra("ID", new long[]{0,0,0});
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);
            cal.setTimeInMillis(System.currentTimeMillis()+(diff2+500));
            alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        }
        //if(Show==0)
        new CountDownTimer(4000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        }.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
