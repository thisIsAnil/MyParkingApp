package com.infi.myparkingapp_client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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
import java.util.Calendar;
import java.util.List;


/**
 * Created by INFIi on 6/9/2016.
 */
public class GpsRvAdapter extends RecyclerView.Adapter<GpsRvAdapter.MyHolder>{
    private List<GPS_data_class> mList=new ArrayList<>();
    private final TypedValue mTypedValue=new TypedValue();
    private int mBackground;
    Context c;
    int lastPosition=-1;

    public class MyHolder extends RecyclerView.ViewHolder{
        public TextView label,address;
        public Switch on_off;
        View mView;
        RelativeLayout container;
        public MyHolder(View v){
            super(v);
            mView=v;
            container=(RelativeLayout)v.findViewById(R.id.container_gps);
            label=(TextView)v.findViewById(R.id.label_gps_rv);
            address=(TextView)v.findViewById(R.id.address_rv);
            on_off=(Switch)v.findViewById(R.id.switch_gps_rv);
        }
        public void clearAnimation()
        {
            mView.clearAnimation();
        }
    }
        public GpsRvAdapter(Context context,List<GPS_data_class> list){
            context.getTheme().resolveAttribute(mBackground,mTypedValue,true);
            mBackground= mTypedValue.resourceId;
            this.c=context;
            this.mList=list;
        }
    @Override
    public GpsRvAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.gps_rv_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onViewDetachedFromWindow(MyHolder holder) {
        ((MyHolder)holder).clearAnimation();
    }

    private void animate(View v, int position){
        if(position>lastPosition){
            Animation animation= AnimationUtils.loadAnimation(c,android.R.anim.slide_in_left);
            animation.setDuration(300);
            v.setAnimation(animation);
            lastPosition=position;
        }
    }
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final GPS_data_class data=mList.get(position);
        boolean isChecked=false;
        animate(holder.container,position);
        holder.address.setVisibility(View.VISIBLE);
        holder.label.setVisibility(View.VISIBLE);
        holder.on_off.setVisibility(View.VISIBLE);

        final String lat=data.getLatitude();
        final String lon=data.getLongitude();

        final String address=data.getAddress();
        final String label=data.getTitle();
        final int Status=data.getStatus();
        final String Radius=data.getRadius();
        final long id=data.getId();
        if(Status==1){isChecked=true;}

        holder.label.setText(label);
        holder.address.setText(address);
        holder.on_off.setChecked(isChecked);

        holder.on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                c.stopService(new Intent(c,GpsService.class));


                        TimerAdapter dbHelper=new TimerAdapter(c);
                        try{dbHelper.open();}catch (SQLException e){
                            e.printStackTrace();
                        }
                int val;
                if(isChecked){val=1;}else val=0;
                        dbHelper.update_gps(data.getId(),data.getTitle(),data.getLatitude(),data.getLongitude(),data.getAddress(),data.getRadius(),data.getMode(),val);
                        Intent myIntent = new Intent(c, GpsBroadcastReciever.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(c,0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

                        AlarmManager alarmManager = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);

                        Calendar c=Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pendingIntent);




            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context=v.getContext();
                ArrayList<String> stringList=new ArrayList<String>();
                stringList.add(0,label);
                stringList.add(1,lat);
                stringList.add(2,lon);
                stringList.add(3,address);
                if(Status==1){stringList.add(4,"Active");}
                else{stringList.add(4,"InActive");}
                stringList.add(5,Radius);
                stringList.add(6,""+id+"");
                stringList.add(7,data.getMode());
                Intent intent=new Intent(context,Gps_detail.class);
                intent.putExtra("getX",v.getWidth()/2);
                intent.putExtra("getY",(int)v.getY());
                intent.putStringArrayListExtra("gps_data",stringList);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int removeItem(int position){
        if(position!=0&&position<mList.size()) {
            mList.remove(position);
            notifyItemRangeChanged(position, mList.size());
            notifyItemRemoved(position);
            return 1;
        }else  if(position>=mList.size()&&mList.size()>0){
            mList.remove(mList.size()-1);
            return 1;
        }
        else{
            return 0;
        }
    }
}
