package com.infi.myparkingapp_client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Infi on 3/31/2016.
 */
public class TimerData {

   // public static final String TABLE="Timer";

   /*public static final String KEY_ID="id";
    public static final String KEY_START_HR="start hr";
    public static final String KEY_STOP_HR="stop hr";
    public static final String KEY_START_MIN="start_min";
    public static final String KEY_STOP_MIN="stop_min";
    public static final String KEY_DAYS="days";
*/

    private String start_hr,stop_hr,start_min,stop_min;
    private String days,isDaily;
    private int status;private long id;
    private String Title;
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStart_hr(){
        return start_hr;
    }
    public String getStop_hr(){
        return stop_hr;
    }
    public String getStart_min(){
        return start_min;
    }
    public String getStop_min(){
        return stop_min;
    }
    public void setStart_hr(String start_hr){
        this.start_hr=start_hr;
    }
    public void setStop_hr(String stop_hr){
        this.stop_hr=stop_hr;
    }
    public void setStatus(int st){this.status=st;}


    public void setIsDaily(String isDaily) {
        this.isDaily = isDaily;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitle() {
        return Title;
    }


    public void setStart_min(String start_min){
        this.start_min=start_min;
    }
    public void setStop_min(String stop_min){
        this.stop_min=stop_min;
    }
    public void setDays(String day){this.days=day;}
    public String getDays(){
        return days;
    }
    public int getStatus(){return status;}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsDaily() {
        return isDaily;
    }


}
