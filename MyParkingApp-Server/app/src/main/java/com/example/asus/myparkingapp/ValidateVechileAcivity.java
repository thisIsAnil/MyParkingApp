package com.example.asus.myparkingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Asus on 18-09-2016.
 */
public class ValidateVechileAcivity extends Activity {
        private VechileData datasource;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_vehicle);

            datasource = new VechileData(this);
            datasource.open();

            List<Vehicles> values = datasource.getAllEntry();

            // use the SimpleCursorAdapter to show the
            // elements in a ListView
            ListAdapter adapter=new ListAdapter(this,values);
            ListView listView=(ListView)findViewById(R.id.vehicle_list);
            listView.setAdapter(adapter);
        }

        // Will be called via the onClick attribute
        // of the buttons in main.xml
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.add_vehicle_event:
                    startActivity(new Intent(ValidateVechileAcivity.this,RegisterVehicleActivity.class));
                    break;
            }
        }

        @Override
        protected void onResume() {
            datasource.open();
            super.onResume();
        }

        @Override
        protected void onPause() {
            datasource.close();
            super.onPause();
        }
    private class ListAdapter extends BaseAdapter{
        private List<Vehicles> list;
        private Context context;
        private View view;
        public ListAdapter(Context context,List<Vehicles> list){
            this.list=list;
            this.context=context;}
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=new ViewHolder();
            if(convertView==null){
                convertView=LayoutInflater.from(context).inflate(R.layout.vechile_list_row,parent,false);
                holder.num=(TextView) convertView.findViewById(R.id.vehicle_number);
                holder.type=(TextView)convertView.findViewById(R.id.vehicle_type);
            }
            holder.num.setText("Vehicle No:"+list.get(position).getNAME());
            holder.type.setText("Type:"+list.get(position).getTYPE());
            return convertView;
        }
    }
    public class ViewHolder{
        private TextView type,num;
    }
}

