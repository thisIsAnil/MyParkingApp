package com.example.asus.myparkingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.example.asus.myparkingapp.FaceVerification.ui.IdentificationActivity;

import java.util.Set;

/**
 * Created by Asus on 18-09-2016.
 */
public class SettingActivity extends AppCompatActivity {
    private TextView mode,area,road,slot,lane;
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_settings);
        Toolbar tbr = (Toolbar) findViewById(R.id.tb_setting);
        setSupportActionBar(tbr);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final Context mContext=this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {

            setupDrawerContent(navigationView);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
       mode=(TextView)findViewById(R.id.mode_text);
        area=(TextView)findViewById(R.id.area_text);
        slot=(TextView)findViewById(R.id.slot_text);
        road=(TextView)findViewById(R.id.road_text);
        lane=(TextView)findViewById(R.id.lane_text);
        SharedPreferences preferences=getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE);
        BoolPref boolPref=new BoolPref(preferences,KEYS.MODE,true);
        if (boolPref.getBooleanPref())mode.setText("Face Detection");
        else mode.setText("FingerPrint");
        StringPref stringPref=new StringPref(preferences,KEYS.Area,"400X200");
        area.setText(stringPref.getStringPref()+"");
        stringPref=new StringPref(preferences,KEYS.SLOT_WIDTH,"10");
        slot.setText(stringPref.getStringPref()+"");
        stringPref=new StringPref(preferences,KEYS.ROAD_WIDTH,"10");
        road.setText(stringPref.getStringPref()+"");
        stringPref=new StringPref(preferences,KEYS.LANE_WIDTH,"10");
        lane.setText(stringPref.getStringPref()+"");

        boolPref=new BoolPref(preferences,KEYS.AUTOMATIC,false);
        final FrameLayout Fview=(FrameLayout)findViewById(R.id.frame_view);
        findViewById(R.id.mode_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items={"Face Detection","FingerPrint Verification","Vehicle No. Plate Verification"};
                PopupWindow popupWindow=setPopupWindow(items,mode,true,false,-1);
                popupWindow.showAtLocation(Fview, Gravity.CENTER,0,0);
            }
        });
        findViewById(R.id.area_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items={"200X400","400X200","800X600","1000X800"};
                PopupWindow popupWindow=setPopupWindow(items,area,true,false,-1);
                popupWindow.showAtLocation(Fview, Gravity.CENTER,0,0);
            }
        });
        findViewById(R.id.road_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items={"10","20","30","40","50"};
                PopupWindow popupWindow=setPopupWindow(items,road,false,true,1);
                popupWindow.showAtLocation(Fview, Gravity.CENTER,0,0);
            }
        });
        findViewById(R.id.slot_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items={"10","20","30","40","50"};
                PopupWindow popupWindow=setPopupWindow(items,slot,false,true,2);
                popupWindow.showAtLocation(Fview, Gravity.CENTER,0,0);
            }
        });
        findViewById(R.id.lane_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items={"10","20","30","40","50"};
                PopupWindow popupWindow=setPopupWindow(items,lane,false,true,3);
                popupWindow.showAtLocation(Fview, Gravity.CENTER,0,0);
            }
        });
        Switch s=(Switch)findViewById(R.id.operating_text);
        if(boolPref.getBooleanPref())s.setChecked(true);
        else s.setChecked(false);
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    BoolPref boolPref=new BoolPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.AUTOMATIC,false);
                boolPref.setBooleanPref(b);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(SettingActivity.this, IdentificationActivity.class));
                        return true;
                    case R.id.nav_messages:
                        //startActivity(new Intent(SettingActivity.this,SettingActivity.class));
                        return true;
                    case R.id.more:
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        return true;
                    case R.id.camera:
                        startActivity(new Intent(SettingActivity.this, CameraActivity.class));
                        return true;
                }
                return false;

            }
        });

    }
    private PopupWindow setPopupWindow(final String[] items, final TextView textView, final boolean isBoolPref, final boolean isIntPref, final int type){
        final PopupWindow popup=new PopupWindow(SettingActivity.this);
        ListView listView=new ListView(SettingActivity.this);
        listView.setAdapter(setPopUpAdapter(items));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                popup.dismiss();
                textView.setText(items[position]);
                SharedPreferences preferences=getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE);
                if(isBoolPref){
                    BoolPref boolPref=new BoolPref(preferences,KEYS.MODE,false);
                    boolean val;
                    if(position==1)val=true;
                    else val=false;
                    boolPref.setBooleanPref(val);
                }
                else if(isIntPref){
                    if(type==1){
                        StringPref stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.ROAD_WIDTH,"10");
                    stringPref.setStringPref(items[position]);
                    }else if(type==2){
                        StringPref stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.SLOT_WIDTH,"10");
                        stringPref.setStringPref(items[position]);
                    }else if(type==3){
                        StringPref stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.LANE_WIDTH,"10");
                        stringPref.setStringPref(items[position]);
                    }
                }else {
                    StringPref stringPref=new StringPref(preferences,KEYS.Area,items[position]);
                    stringPref.setStringPref(items[position]);
                    stringPref=new StringPref(preferences,KEYS.Area,"400X200");
                    String s=stringPref.getStringPref();
                    String[] strings=s.split("X");
                    int length=Integer.parseInt(strings[0]);
                    int width=Integer.parseInt(strings[1]);
                    CreateSlots(length,width);
                }

            }
        });
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setAnimationStyle(android.R.style.Animation_Dialog);
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#fafafa")));
        popup.setContentView(listView);
        popup.setFocusable(true);
        //popup.setElevation(11f);
        popup.setOutsideTouchable(true);
        popup.setTouchable(true);
        return popup;
    }

    private ArrayAdapter<String> setPopUpAdapter(String[] itemsList){
        final String[] contents=itemsList;
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SettingActivity.this,android.R.layout.simple_list_item_single_choice,itemsList){
            @Override
            public int getPosition(String item) {
                for(int i=0;i<contents.length;i++){
                    if(contents[i].equals(item))return i;
                }
                return contents.length;
            }

            @Override
            public String getItem(int position) {
                return contents[position];
            }

            @Override
            public int getCount() {
                return contents.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final String text = getItem(position);

                // visual settings for the list item
                TextView listItem = new TextView(SettingActivity.this);
                listItem.setGravity(Gravity.CENTER);
                listItem.setText(text);
                listItem.setTag(position);
                listItem.setTextSize(22);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.GRAY);
                return listItem;
            }
        };
        return adapter;
    }


    public void CreateSlots(int length,int width){
        Measures measures=new Measures(SettingActivity.this);
        int lanes=length/(measures.ROAD_WIDTH+measures.LANE_WIDTH);
        int slots=(width-(2*measures.ROAD_WIDTH))/measures.SLOT_WIDTH;
        StringPref stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.LANES,lanes+"");
        stringPref.setStringPref(lanes+"");
        stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.SLOTS_NO,slots+"");
        stringPref.setStringPref(slots+"");
        String emptySlots = "";
        for (int i = 0; i < lanes*(slots - 1); i++) {
            emptySlots+="0";
        }
        stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.EMPTY_SLOTS,emptySlots);
        stringPref.setStringPref(emptySlots);
    }

}
