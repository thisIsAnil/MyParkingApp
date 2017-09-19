package com.infi.myparkingapp_client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by INFIi on 6/9/2016.
 */
public class Gps_list_Fragment extends android.support.v4.app.Fragment {
    GpsRvAdapter adapter;
    private Paint p=new Paint();
    RecyclerView rv;
    List<GPS_data_class> list=new ArrayList<>();
    TimerAdapter dbHelper;

    private  void ScheduleStuff(Context c){

        Intent g=new Intent(c,GpsBroadcastReciever.class);
        PendingIntent pd=PendingIntent.getBroadcast(c,0,g,0);
        AlarmManager alarm=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pd);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
         rv=(RecyclerView)inflater.inflate(R.layout.recycler_view,container,false);

        setUpList(rv);
        initSwipe();
        return rv;
    }
    public void setUpList(RecyclerView recyclerView){
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        list=getListItems();
        if(list.isEmpty()){
            recyclerView.setVisibility(View.GONE);

        }
        else{
            AudioManager audioManager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
            //if(audioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT)
                ScheduleStuff(getActivity());
            adapter=new GpsRvAdapter(getActivity(),list);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);}
    }
    public List<GPS_data_class> getListItems(){
        List<GPS_data_class> data=new ArrayList<>();
        dbHelper=new TimerAdapter(getActivity());
        Cursor c;
        c=dbHelper.getAllGpsEvents();

        if(c.moveToFirst()) {
            do {
                GPS_data_class event=new GPS_data_class();
                event.setTitle(c.getString(c.getColumnIndex(TimerAdapter.KEY_GPS_TITLE)));
                event.setLatitude(c.getString(c.getColumnIndex(TimerAdapter.KEY_LAT)));
                event.setLongitude(c.getString(c.getColumnIndex(TimerAdapter.KEY_LON)));
                event.setAddress(c.getString(c.getColumnIndex(TimerAdapter.KEY_ADDRESS)));
                event.setStatus(c.getInt(c.getColumnIndex(TimerAdapter.KEY_STATUS_GPS)));
                event.setRadius(c.getString(c.getColumnIndex(TimerAdapter.KEY_RADIUS)));
                event.setId(c.getLong(c.getColumnIndex(TimerAdapter.KEY_ID_GPS)));
                event.setMode(c.getString(c.getColumnIndex(TimerAdapter.KEY_MODE_GPS)));
                //timer.setIsDaily(c.getString(c.getColumnIndex(TimerAdapter.KEY_DAILY)));
                data.add(event);
            } while (c.moveToNext());
            c.close();
        }
        if(data.size()==0){
        }
        return data;
    }
    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT||direction==ItemTouchHelper.RIGHT){
                    long id=list.get(position).getId();
                    dbHelper.deleteGps(id);
                    Context c=getActivity();
                    c.stopService(new Intent(c,GpsService.class));
                    Intent myIntent = new Intent(c, GpsBroadcastReciever.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c,0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);

                    Calendar cal=Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), pendingIntent);
                    int i=adapter.removeItem(position);
                    if(i==0){rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));rv.setVisibility(View.GONE);}

                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#8554C5"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_black_48dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#8554C5"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_black_48dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */

}
