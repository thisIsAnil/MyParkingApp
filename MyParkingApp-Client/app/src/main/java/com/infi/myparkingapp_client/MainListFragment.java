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
import android.widget.ArrayAdapter;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Infi on 5/7/2016.
 */
public class MainListFragment extends android.support.v4.app.Fragment {
    RecyclerView rv;
    TimerAdapter dbHelper;
    ListAdapter adapter;
    private Paint p=new Paint();
    List<TimerData> list;
    public void ScheduleStuff(Context c){
        Intent i=new Intent(c,TimerBroadCastReciever.class);
        i.putExtra("ID",new long[]{0,0,0});

        PendingIntent p=PendingIntent.getBroadcast(c,0,i,0);
        AlarmManager alarmManager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),p);


    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    rv=(RecyclerView)inflater.inflate(R.layout.recycler_view,container,false);
    dbHelper=new TimerAdapter(getActivity());
        try{dbHelper.open();}catch (SQLException e){
            e.printStackTrace();
        }
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
           //AudioManager audioManager=(AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
           //if(audioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT)
           //ScheduleStuff(getActivity());
           adapter=new ListAdapter(getActivity(),list);
           recyclerView.setVisibility(View.VISIBLE);
           recyclerView.setAdapter(adapter);


       }
    }
    public List<TimerData> getListItems(){
        List<TimerData> data=new ArrayList<>();
        TimerAdapter adapter=new TimerAdapter(getActivity());
        Cursor c;
      c=adapter.get_all_events();

        if(c.moveToFirst()) {
            do {
                TimerData timer = new TimerData();
                timer.setStart_hr(c.getString(c.getColumnIndex(TimerAdapter.KEY_START_HR)));
                timer.setStart_min(c.getString(c.getColumnIndex(TimerAdapter.KEY_START_MIN)));
                timer.setStop_hr(c.getString(c.getColumnIndex(TimerAdapter.KEY_STOP_HR)));
                timer.setStop_min(c.getString(c.getColumnIndex(TimerAdapter.KEY_STOP_MIN)));
                timer.setDays(c.getString(c.getColumnIndex(TimerAdapter.KEY_DAYS)));
                timer.setStatus(c.getInt(c.getColumnIndex(TimerAdapter.KEY_STATUS)));
                timer.setTitle(c.getString(c.getColumnIndex(TimerAdapter.KEY_TITLE)));
                timer.setId(c.getLong(c.getColumnIndex(TimerAdapter.KEY_ID)));
                timer.setMode(c.getString(c.getColumnIndex(TimerAdapter.KEY_MODE)));
                timer.setIsDaily(c.getString(c.getColumnIndex(TimerAdapter.KEY_DAILY)));
               //Toast.makeText(getActivity(), "Displaying"+timer.getTitle()+timer.days+timer.getId()+"", Toast.LENGTH_SHORT).show();
                data.add(timer);
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
                    dbHelper.deleteRow(id);


                    int i=adapter.removeItem(position);
                    if (i == 0) {
                        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));rv.setVisibility(View.GONE);
                    }
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


}
