package com.infi.myparkingapp_client;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;


import android.support.v7.widget.RecyclerView;

import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Infi on 4/28/2016.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyListHolder> {
    private List<TimerData> timers;
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    public Context c;
    int lastPosition=-1;
    public class MyListHolder extends RecyclerView.ViewHolder{
        public final TextView start,stop;
        public final Switch on_off;
        RelativeLayout container;
       // public final ImageView isDaily;
        public final  View mView;
        public final TextView t_start,t_end,t_title;
        public MyListHolder(View view){
            super(view);
            container=(RelativeLayout)view.findViewById(R.id.container_timer);
            mView=view;
            start=(TextView)view.findViewById(R.id.start_text);
            stop=(TextView)view.findViewById(R.id.stop_text);
            on_off=(Switch) view.findViewById(R.id.switch_btn);
            //isDaily=(ImageView)view.findViewById(R.id.isDaily);
            t_start=(TextView)view.findViewById(R.id.title_start);
            t_end=(TextView)view.findViewById(R.id.title_stop);
            t_title=(TextView)view.findViewById(R.id.title_label);
        }
        public void clearAnimation()
        {
            mView.clearAnimation();
        }
    }
    public ListAdapter(Context context, List<TimerData> t){

        context.getTheme().resolveAttribute(mBackground,mTypedValue,true);
        mBackground=mTypedValue.resourceId;
        this.timers=t;
        c=context;
    }
    @Override
    public MyListHolder onCreateViewHolder(ViewGroup parent,int viewtype){
View itemview= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
        return new MyListHolder(itemview);

    }

    @Override
    public void onViewDetachedFromWindow(MyListHolder holder) {
        ((MyListHolder)holder).clearAnimation();
    }

    private void animate(View viewToAnimate, int position){
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(c, R.anim.fade_in);
            animation.setDuration(400);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public void onBindViewHolder(MyListHolder holder,int position){
        final int pos=position;
        boolean ischecked=false;
        animate(holder.container,position);
        final TimerData time = timers.get(position);


            holder.start.setVisibility(View.VISIBLE);
            holder.stop.setVisibility(View.VISIBLE);
            holder.on_off.setVisibility(View.VISIBLE);

            final String start_time,stop_time,title;
            title=time.getTitle();
            start_time=time.getStart_hr() + ":" + time.getStart_min();
            stop_time=time.getStop_hr() + ":" + time.getStop_min();
            holder.t_title.setText(title);
            holder.start.setText(start_time);
            holder.stop.setText(stop_time);

            if (time.getStatus() == 1) ischecked = true;
            holder.on_off.setChecked(ischecked);

            holder.on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){

                            TimerAdapter dbHelper=new TimerAdapter(c);
                            try{dbHelper.open();}catch (SQLException e){
                                e.printStackTrace();
                            }
                            dbHelper.update(time.getId(),time.getTitle(),time.getStart_hr(),time.getStart_min(),time.getStop_hr(),time.getStop_min(),time.getDays(),time.getIsDaily(),time.getMode(),1);

                        Intent myIntent = new Intent(c, TimerBroadCastReciever.class);
                        myIntent.putExtra("ID",new long[]{time.getId(),0,0});

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,0, myIntent,0);

                        AlarmManager alarmManager = (AlarmManager)c.getApplicationContext().getSystemService(Context.ALARM_SERVICE);


                        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), pendingIntent);


                    }
                    else{
                            TimerAdapter dbHelper=new TimerAdapter(c);
                            try{dbHelper.open();}catch (SQLException e){
                                e.printStackTrace();
                            }
                            dbHelper.update(time.getId(),time.getTitle(),time.getStart_hr(),time.getStart_min(),time.getStop_hr(),time.getStop_min(),time.getDays(),time.getIsDaily(),time.getMode(),0);
                        Intent myIntent = new Intent(c, TimerBroadCastReciever.class);
                        myIntent.putExtra("ID",new long[]{time.getId(),0,1});

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,0, myIntent,0);

                        AlarmManager alarmManager = (AlarmManager)c.getApplicationContext().getSystemService(Context.ALARM_SERVICE);


                        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), pendingIntent);

                    }
                    //if(isChecked) {c.startService(new Intent(c,TimerBroadCastReciever.class));};
                }
            });
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context=v.getContext();
                    ArrayList<String> list=new ArrayList<String>();
                    list.add(0,title);
                    list.add(1,start_time);
                    list.add(2,stop_time);
                    list.add(3,time.getDays());
                    list.add(4,time.getIsDaily());
                    if(time.getStatus()==1)
                    {list.add(5,"Active");}
                    else{list.add(5,"InActive");}
                    list.add(6,""+time.getId()+"");
                    list.add(7,time.getMode());
                    list.add(8,time.getStart_hr());
                    list.add(9,time.getStart_min());
                    list.add(10,time.getStop_hr());
                    list.add(11,time.getStop_min());
                    Intent i=new Intent(context,main_Detail.class);
                    i.putExtra("getX",v.getWidth()/2);
                    i.putExtra("getY",(int)v.getY());
                    i.putStringArrayListExtra("get_data",list);
                    context.startActivity(i);
                }
            });
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);


                    return true;
                }
            });
    }

    @Override
    public int getItemCount(){
        return timers.size();
    }

    public int removeItem(int position){
        if(position!=0||position<timers.size()) {
            timers.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, timers.size());
            return 1;
        }
        else if(position>=timers.size()&&timers.size()>0){timers.remove(timers.size()-1);return 1;}
        else {
            return 0;
        }
    }
}
