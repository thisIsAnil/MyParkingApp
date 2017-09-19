package com.infi.myparkingapp_client;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by INFIi on 5/30/2016.
 */
public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.MyHolder> {
    List<SetTimeClass> setTime;
    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;

    public class MyHolder extends RecyclerView.ViewHolder{
        public final TextView label,selected;
        // public final ImageView isDaily;
        public final View mView;

        public MyHolder(View view){
            super(view);
            mView=view;
            label=(TextView)view.findViewById(R.id.list_title);
            selected=(TextView)view.findViewById(R.id.list_selected);
             }

    }
    public TimerListAdapter(Context context, List<SetTimeClass> t){

        context.getTheme().resolveAttribute(mBackground,mTypedValue,true);
        mBackground=mTypedValue.resourceId;
        this.setTime=t;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewtype){
        View itemview= LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_list_item,parent,false);
        return new MyHolder(itemview);

    }
    @Override
    public void onBindViewHolder(MyHolder holder,int position){
        boolean ischecked=false;
        SetTimeClass data=setTime.get(position);
        holder.label.setText(data.getLabel());
        holder.selected.setText(data.getSelected());

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.
            }
        });*/
    }

    @Override
    public int getItemCount(){
        return setTime.size();
    }

}
