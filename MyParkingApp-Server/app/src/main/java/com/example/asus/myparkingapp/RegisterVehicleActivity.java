package com.example.asus.myparkingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Asus on 18-09-2016.
 */
public class RegisterVehicleActivity extends Activity {
    TextView type;EditText number;
    private VechileData datasource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);

        datasource = new VechileData(this);
        datasource.open();

        type=(TextView)findViewById(R.id.type_of_vehicle);
        number=(EditText)findViewById(R.id.num_of_vehicle);
        type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                PopupWindow popupWindow=new PopupWindow();
                final ListView listView=new ListView(RegisterVehicleActivity.this);
                final String[] items={"2 Wheeler","4Wheeler"};
                listView.setAdapter(Adapter(items));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        type.setText(items[position-1]);
                    }
                });
                popupWindow.setFocusable(true);
                popupWindow.setWidth(250);
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

                // set the list view as pop up window content
                popupWindow.setContentView(listView);
                return false;
            }
        });

        findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasource.createVehicleEntry(type.getText().toString(),number.getText().toString());
            }
        });
        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterVehicleActivity.this,ValidateVechileAcivity.class));
            }
        });
    }
    private ArrayAdapter<String> Adapter(String dogsArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dogsArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
                String item = getItem(position);
                String[] itemArr = item.split("::");
                String text = itemArr[0];
                String id = itemArr[1];

                // visual settings for the list item
                TextView listItem = new TextView(RegisterVehicleActivity.this);

                listItem.setText(text);
                listItem.setTag(id);
                listItem.setTextSize(22);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.WHITE);

                return listItem;
            }
        };

        return adapter;
    }
}
