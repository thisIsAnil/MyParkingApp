package com.infi.myparkingapp_client;
import android.app.AlarmManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.media.AudioManager;
import android.os.IBinder;


import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class TimerService extends Service {
    boolean Candelte=false;
    long DeleteId;
    TimerAdapter dbHelper;
    public TimerService() {

    }

    long id=0;
    Calendar cal=Calendar.getInstance();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return  null;
    }

    @Override
    public void onCreate() {
        Log.d(this.getClass().getSimpleName(), "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long[] data=intent.getLongArrayExtra("ID");
        DeleteId=data[0];
        //Toast.makeText(getApplicationContext(),data[0]+" ",Toast.LENGTH_SHORT).show();
        if(data[1]==0){
            Candelte=true;
        }
        if(data[2]==1){
            cancelAlarm();
        }else {
            ScheduleAlarm();
        }
        stopSelf();
        return START_STICKY;
    }

    private TimerData getNext() {
        Set<TimerData> TimerQueue = new TreeSet<TimerData>(new Comparator<TimerData>() {
            @Override
            public int compare(TimerData lhs, TimerData rhs) {
                int result = 0;
                cal=Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                long cur_time=(cal.get(Calendar.HOUR_OF_DAY)*60*60*1000)+(cal.get(Calendar.MINUTE)*60*1000)+(cal.get(Calendar.SECOND)*1000);
                long diff = convertStartTime(lhs) - convertStartTime(rhs);
                long tmp=convertStopTime(lhs)-cur_time;
                long t2=convertStopTime(rhs)-cur_time;

                if (diff < 0&&tmp>0) {
                    return -1;
                } else if (diff > 0&&t2>0) {
                    return 1;
                }else if(diff==0&&tmp<t2&&tmp>0)return -1;
                else if(diff==0&&tmp>=t2&&t2>0)return 1;
                return result;
            }
        });

        dbHelper = new TimerAdapter(this);
        try {
            dbHelper.open();
        } catch (Exception e) {
            Log.e("TAG", "Cannot Open Db");
            e.printStackTrace();
        }

        Cursor c = dbHelper.get_all_events();
        List<TimerData> list = new ArrayList<>();
        if (c != null) {
            do {
                TimerData timer = new TimerData();
                   timer.setStart_hr(c.getString(c.getColumnIndex(TimerAdapter.KEY_START_HR)));
                    timer.setStart_min(c.getString(c.getColumnIndex(TimerAdapter.KEY_START_MIN)));
                    timer.setStop_hr(c.getString(c.getColumnIndex(TimerAdapter.KEY_STOP_HR)));
                    timer.setStop_min(c.getString(c.getColumnIndex(TimerAdapter.KEY_STOP_MIN)));
                    timer.setDays(c.getString(c.getColumnIndex(TimerAdapter.KEY_DAYS)));
                    timer.setTitle(c.getString(c.getColumnIndex(TimerAdapter.KEY_TITLE)));
                    timer.setStatus(c.getInt(c.getColumnIndex(TimerAdapter.KEY_STATUS)));
                    timer.setId(c.getInt(c.getColumnIndex(TimerAdapter.KEY_ID)));
                    timer.setIsDaily(c.getString(c.getColumnIndex(TimerAdapter.KEY_DAILY)));
                    timer.setMode(c.getString(c.getColumnIndex(TimerAdapter.KEY_MODE)));
                cal=Calendar.getInstance();
                char[] t=timer.getDays().toCharArray();
                int t2=cal.get(Calendar.DAY_OF_WEEK);
                long cur_time=(cal.get(Calendar.HOUR_OF_DAY)*60*60*1000)+(cal.get(Calendar.MINUTE)*60*1000)+(cal.get(Calendar.SECOND)*1000);
                long tmp=convertStopTime(timer)-cur_time;

                if(timer.getStatus()==1&&t[t2-1]=='1'&&tmp>0)
                list.add(timer);
            } while (c.moveToNext());
        }
        else{stopSelf();}
        if(list.size()==0){stopSelf();}
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus() == 1)
                TimerQueue.add(list.get(i));
        }
        if(list.size()!=0) {
            if (TimerQueue.iterator().hasNext()) {
                return TimerQueue.iterator().next();
            }
        }
        else {
            return null;
        }
        dbHelper.close();
        return null;

    }


    public long convertStartTime(TimerData data) {
        int s1_hr, s1_min;
        String s1_am=" ";
        s1_hr = Integer.parseInt(data.getStart_hr());
        char[] t1 = data.getStart_min().toCharArray();
        s1_min = Integer.parseInt(""+t1[0] + t1[1] + "");
        s1_am = ""+t1[2] + t1[3] + "";
        if (s1_am.equals("PM")&&s1_hr!=12) {
            s1_hr += 12;
        }else if(s1_am.equals("AM")&&s1_hr==12){
            s1_hr=0;
        }
        s1_hr = (s1_hr  *60*60 * 1000);
        s1_min = (s1_min  *60* 1000);
        return s1_hr + s1_min;

    }

    public long convertStopTime(TimerData data) {
        int s1_hr, s1_min;
        String s1_am="";
        s1_hr = Integer.parseInt(data.getStop_hr());
        char[] t1 = data.getStop_min().toCharArray();
        s1_min = Integer.parseInt(""+t1[0] + t1[1] + "");
        s1_am = ""+t1[2] + t1[3] + "";
        if (s1_am.equals("PM")&&s1_hr!=12) {
            s1_hr += 12;
        }else if(s1_am.equals("AM")&&s1_hr==12){
            s1_hr=0;
        }
        s1_hr = (s1_hr *60 * 60 * 1000);
        s1_min = (s1_min*60 * 1000);
        return s1_hr + s1_min;

    }


    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        /*Intent i=new Intent(getApplicationContext(),TimerBroadCastReciever.class);
        //i.putExtra("Time",data);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),0,i,PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal=Calendar.getInstance();
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC,cal.getTimeInMillis(),pendingIntent);*/
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */

    public long[] convertMode(String days){
        char[] s=days.toCharArray();
        long a[]=new long[4];
        for (int i=0;i<4;i++){
            if(s[i]=='1'){a[i]=1;}
        }
        return a;

    }

public void cancelAlarm(){

    Intent myIntent = new Intent(getApplicationContext(), SilentBroadCastReciever.class);
    //myIntent.putExtra("alarm", new Timer());
    myIntent.putExtra("Time", new long[3]);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);
    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

    alarmManager.cancel(pendingIntent);
    stopSelf();
}

    private void ScheduleAlarm(){
        long diff2;
        TimerData timer = getNext();
         long startTime;
        long stopTime;
        cal=Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        long cur_time=(cal.get(Calendar.HOUR_OF_DAY)*60*60*1000)+(cal.get(Calendar.MINUTE)*60*1000)+(cal.get(Calendar.SECOND)*1000);
        if(null!=timer){
                    long[] modes=convertMode(timer.getMode());

                 AudioManager mode = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                      startTime = convertStartTime(timer);
                      stopTime = convertStopTime(timer);
                      if(startTime-cur_time<=1000&&mode.getRingerMode()!=AudioManager.RINGER_MODE_SILENT&&stopTime-cur_time>=5000&&modes[0]==1){
                         mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                          mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                          }

                      Intent myIntent = new Intent(getApplicationContext(), SilentBroadCastReciever.class);
                      //myIntent.putExtra("alarm", new Timer());


                      myIntent.putExtra("Stop", new long[]{stopTime,modes[0],modes[1],modes[2],modes[3]});
                      PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                      AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                      Calendar calendar = Calendar.getInstance();
                      calendar.setTimeInMillis(System.currentTimeMillis());
                      char[] sd = timer.getDays().toCharArray();
                      for (int i = 0; i < sd.length; i++) {
                          if (sd[i] == '1') {
                              int cur = calendar.get(Calendar.DAY_OF_WEEK);
                              int val = (Calendar.SATURDAY - cur + i + 1) % 7;
                              if (val != 0)
                              calendar.add(Calendar.DAY_OF_YEAR, val);
                          }
                      }
                      int s1_hr = 0, s1_min = 0;
                      String s1_am;
                      s1_hr = Integer.parseInt(timer.getStart_hr());
                      char[] t1 = timer.getStart_min().toCharArray();
                      String s = "" + t1[0] + t1[1] + "";
                      s1_min = Integer.parseInt(s);
                      s1_am = "" + t1[2] + t1[3] + "";
                      String check = "" + "PM" + "";
                      if (s1_am.equals(check)&&s1_hr!=12) {

                          s1_hr = s1_hr + 12;
                      }
                      check = "" + "AM" + "";
                      if (s1_am.equals(check)) {
                          if (s1_hr == 12) s1_hr = 0;
                      }
                      if((stopTime-cur_time)>0) {
                              if (startTime - cur_time > 0) {
                                  calendar.set(Calendar.HOUR_OF_DAY, s1_hr);
                                  calendar.set(Calendar.MINUTE, s1_min);
                                  calendar.set(Calendar.SECOND, 0);
                                  calendar.set(Calendar.MILLISECOND, 0);
                              } else {
                                  calendar.setTimeInMillis(System.currentTimeMillis());
                              }


                      }else {
                          calendar.add(Calendar.DAY_OF_YEAR, 1);
                          calendar.set(Calendar.HOUR_OF_DAY, s1_hr);
                          calendar.set(Calendar.MINUTE, s1_min);
                          calendar.set(Calendar.SECOND,0);
                          calendar.set(Calendar.MILLISECOND,0);
                          //Toast.makeText(getApplicationContext(), "Cal set tomo ", Toast.LENGTH_SHORT).show();
                      }

                      alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        // Toast.makeText(getApplicationContext(), timer.getId()+"  Actual\n"+DeleteId, Toast.LENGTH_SHORT).show();
                      if(Candelte&&(timer.getId()==DeleteId)||timer.getIsDaily().equals("N")){
                          dbHelper = new TimerAdapter(getApplicationContext());
                          try {
                              dbHelper.open();
                          } catch (Exception e) {
                              Log.e("TAG", "Cannot Open Db");
                              e.printStackTrace();
                          }
                          //dbHelper.deleteRow(timer.getId());
                          dbHelper.update(timer.getId(),timer.getTitle(),timer.getStart_hr(),timer.getStart_min(),timer.getStop_hr(),timer.getStop_min(),timer.getDays(),timer.getIsDaily(),timer.getMode(),0);
                          dbHelper.close();
                      }
                  }


        else {
            cancelAlarm();
        }
    }
}


