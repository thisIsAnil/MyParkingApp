package com.infi.myparkingapp_client;



import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class gps extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener ,LocationListener{
    List<SetTimeClass> mList = new ArrayList<>();
    ArrayList<String> IData = new ArrayList<>();
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    TimerListAdapter mAdapter;
    private GoogleMap mMap;
    boolean[] animationStates;
    Context mContext;
    String mode;
    int[] modes=new int[4];int[] tm=new int[4];
    boolean isEditMode;
    public double mlat;
    public double mlon;
    //public double mAltitude;
    int PrevPosition;
    int sCount = 0, index = 1;
    boolean canExit=false;
    int tmp = 0;
    LinearLayout otherStuff;
    String final_label, final_radius, final_addr, final_lat, final_lon, final_count;
    int final_status;
    Marker finalMarker, tempMarker;
    //double GGA_ALTITUDE = 0d;
    //private static final String NMEA_GGA = "$GPGGA";
    //private static final int altitude_element_id = 9;
    TimerAdapter dbHelper;
    String title;
    //int hint=0;
    int isEdit=0;
    Location lc;
    //boolean nmeaFound = false;
    ColorStateList colorList;
    ListView repeat_list;
    RecyclerView recyclerView;
    final int LABEL_DIALOG_ID = 999;
    final int COUNT_DIALOG_ID = 7854, FLOOR_DIALOG_ID = 555, RADIUS_DIALOG_ID = 12354;
    SupportMapFragment supportMapFragment;
    Circle circle;
    int radius;boolean isNew=false;
    final double[] Alat = new double[100], Alon = new double[100]; //final double[] alti=new double[100];
    String[] customModes=new String[]{"Silent","Normal","WiFi On","WiFi Off"};
    private Geocoder geo;
    private RelativeLayout rl;
    private  AnimateLayout animateLayout;
    boolean isNetConnected=false,isLocationEnabled=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timer_actionBar);
        toolbar.setTitle("Location");
        View rootLayout=(View)findViewById(R.id.animate_gps);
        final Intent intent=getIntent();
        final int w=intent.getIntExtra("getX",0);
        final int h=intent.getIntExtra("getY",0);
        ((TextView)findViewById(R.id.bar_title)).setText("GPS");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

            if (savedInstanceState == null) {
                CircularReveal cr;
                if(w==0||h==0)cr=new CircularReveal(rootLayout.getRootView());
                else cr=new CircularReveal(rootLayout.getRootView(),w,h);
                cr.StartCircularReveal();

            }
        }else{
            Animation cr= AnimationUtils.loadAnimation(gps.this,R.anim.activity_open);
            rootLayout.startAnimation(cr);
        }


        mContext = this;
        dbHelper = new TimerAdapter(this.mContext);
        try {
            dbHelper.open();
        } catch (Exception e) {
            Log.e("TAG", "Cannot Open Db");
            Toast.makeText(mContext, "Cannot Open Db", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        animationStates = new boolean[customModes.length];
        rl=(RelativeLayout)findViewById(R.id.map_frame) ;
        repeat_list=(ListView)findViewById(R.id.repeatList_gps);
        otherStuff=(LinearLayout)findViewById(R.id.lv_layout_gps);
        //Intent i = getIntent();
        IData = intent.getStringArrayListExtra("data");
        if (IData.size() != 0) {
            isEditMode=true;
            //Toast.makeText(mContext,IData.get(8),Toast.LENGTH_SHORT).show();

            final_label = IData.get(0);
            final_lat = IData.get(1);
            final_lon = IData.get(2);
            final_addr = IData.get(3);
            final_radius = IData.get(5);
            radius=Integer.parseInt(final_radius);
            mode="1000";
            char[] t=IData.get(7).toCharArray();
            for(int j=0;j<4;j++){if(t[j]=='1')modes[j]=1;}

            if (IData.get(4) .equals("Active")) final_status = 1;
            else final_status = 0;
            PrevPosition = 0;
            final_count = "1";
            String ln[]=final_lon.split("//_//");String lt[]=final_lat.split("//_//");
            int j=0;boolean check=true;
            do{
                check=false;
                if(j<ln.length-1){check=true;Alon[j]=Double.parseDouble(ln[j]);}
                if(j<lt.length-1){check=true;Alat[j]=Double.parseDouble(lt[j]);index=j;isEdit=j;}
                j++;
            }while(check);



        }
        else {
            isEditMode=false;
            final_label = "Location";
            final_lat = "";
            final_radius = "0";
            final_addr = "No Data Available";
            isEdit=0;
            index=1;
            final_status=1;
            radius=0;
            final_lon = "";
            title = "";
            PrevPosition = 0;
            final_count = "3";
            mode="1000";
            for(int j=0;j<4;j++)modes[j]=0;

        }
        final Button AddEvent = (Button) findViewById(R.id.tb_save_btn);
        final Button CancelEvent = (Button) findViewById(R.id.tb_cancel_btn);
        final Button save = (Button) findViewById(R.id.custom_ok);
        final Button cancel = (Button) findViewById(R.id.custom_cancel);
        mAdapter = new TimerListAdapter(this, mList);
        recyclerView = (RecyclerView) findViewById(R.id.gps_rv);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ListDivider(this, LinearLayoutManager.VERTICAL));
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed},
                new int[]{}
        };

        int[] colors = new int[]{
                Color.RED,
                Color.BLUE,
                Color.RED,
                Color.DKGRAY,
        };
        colorList = new ColorStateList(states, colors);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        supportMapFragment=mapFragment;
        final int width=(getWindowManager().getDefaultDisplay().getWidth())-30;

        new Runnable(){
            @Override
            public void run() {
                animateLayout=new AnimateLayout(supportMapFragment.getView(),width);

            }
        }.run();
        final EditText e = (EditText) findViewById(R.id.search_box);
        getData();
        ImageButton info=(ImageButton)findViewById(R.id.info_gps);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((LinearLayout)findViewById(R.id.hint_layout)).getVisibility()==View.VISIBLE){
                    ((LinearLayout)findViewById(R.id.hint_layout)).setVisibility(View.GONE);
                }
                else ((LinearLayout)findViewById(R.id.hint_layout)).setVisibility(View.VISIBLE);
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext, recyclerView, new Timer.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PrevPosition = position;
                switch (position) {
                    case 0:
                        otherStuff.setVisibility(View.GONE);
                        if(findViewById(R.id.map_layout).getVisibility()==View.VISIBLE);
                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                        findViewById(R.id.map_layout).setClickable(false);
                        showDialog(LABEL_DIALOG_ID);
                        break;
                    case 1:
                        otherStuff.setVisibility(View.GONE);
                        if(findViewById(R.id.map_layout).getVisibility()==View.VISIBLE)
                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                        findViewById(R.id.map_layout).setClickable(false);
                        showDialog(RADIUS_DIALOG_ID);
                        break;
                    case 2:
                        otherStuff.setVisibility(View.GONE);
                        if(findViewById(R.id.map_layout).getVisibility()==View.VISIBLE)
                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                        findViewById(R.id.map_layout).setClickable(false);
                        showDialog(COUNT_DIALOG_ID);

                        break;
                    case 3:
                        otherStuff.setVisibility(View.GONE);
                        sCount=0;index=1;isNew=true;
                        recyclerView.setVisibility(View.GONE);

                        findViewById(R.id.map_layout).setVisibility(View.VISIBLE);
                        animateLayout.setDuration(300);
                        supportMapFragment.getView().startAnimation(animateLayout);
                        save.setText("Ok");
                        cancel.setText("Cancel");
                        if(radius==0) {

                            if (isEdit != 0) {
                                for (int k = 0; k < isEdit; k++) {
                                    getAddress(Alat[k], Alon[k]);
                                    if (k > 0) {
                                        Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(Alat[k - 1],
                                                Alon[k - 1]), new LatLng(Alat[k], Alon[k])).width(25).color(Color.RED));
                                    }
                                }
                                isEdit = 0;
                            }
                            index = 1;
                            tmp = 0;
                        }
                            //rl.setClickable(true);
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(canExit){
                                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        //rl.setClickable(false);
                                        //rl.setVisibility(View.GONE);
                                        //recyclerView.setClickable(true);
                                        SetTimeClass data = mList.get(PrevPosition);
                                        data.setSelected(title);
                                        mList.set(PrevPosition, data);
                                        mAdapter.notifyDataSetChanged();

                                    }
                                    canExit=false;
                                    if (radius==0&&mlat!=0.0&&mlon!=0.0) {
                                        int cnt = Integer.parseInt(final_count);

                                        if (cnt == 1) {
                                            tmp += cnt;
                                        }
                                        sCount++;
                                        if (sCount % cnt != 0||sCount%cnt==0 || tmp == 1) {
                                            Alat[index] = mlat;
                                            Alon[index] = mlon;
                                            index++;
                                            if(isNetConnected){final_addr = title;
                                            finalMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mlat, mlon))
                                                    .title(title).snippet(title).visible(true));}
                                            if (sCount % cnt >= 2 && index <= 10) {

                                                Polyline line = mMap.addPolyline(new PolylineOptions().geodesic(true).add(new LatLng(Alat[index - 2],
                                                        Alon[index - 2]), new LatLng(Alat[index-1], Alon[index-1])).width(10).color(Color.RED));

                                            }
                                            isNew=false;
                                        }
                                        if (sCount % cnt == 0&&cnt!=1) {
                                            canExit=true;
                                            if (sCount % cnt ==0 && index <= 10) {

                                                Polyline line = mMap.addPolyline(new PolylineOptions().geodesic(true).add(new LatLng(Alat[index - 2],
                                                        Alon[index - 2]), new LatLng(Alat[index-1], Alon[index-1])).width(10).color(Color.RED));

                                                line = mMap.addPolyline(new PolylineOptions().geodesic(true).add(new LatLng(Alat[index-1],
                                                       Alon[index-1]), new LatLng(Alat[1], Alon[1])).width(10).color(Color.RED));

                                            }
                                            /*if (Alat[1] != 0.0 && Alon[1] != 0.0 && index != 0) {
                                                double meanLat = 0, meanLon = 0;
                                                for (int i = 1; i < index-1; i++) {
                                                    meanLat += Alat[i];
                                                    meanLon += Alon[i];
                                                }
                                                meanLat = meanLat / (index-1);
                                                meanLon = meanLon / (index-1);
                                                List<Address> lv = new ArrayList<Address>();
                                                int max = 1;
                                                LatLng latLng = new LatLng(meanLat, meanLon);

                                                try {
                                                    lv = geo.getFromLocation(latLng.latitude, latLng.longitude, max);
                                                } catch (IOException e) {
                                                    Log.i("TAG", "Location Changed: ");
                                                    e.printStackTrace();
                                                }
                                                android.location.Address addr = lv.get(0);

                                                StringBuilder str = new StringBuilder();
                                                if (addr != null) {
                                                    for (int j = 0; j <= addr.getMaxAddressLineIndex(); j++) {
                                                        str.append(addr.getAddressLine(j));

                                                        title = str.toString();
                                                    }
                                                    //Toast.makeText(mContext,title, Toast.LENGTH_LONG).show();
                                                }

                                            }*/
                                            isNew=false;

                                        }
                                    }
                                    else if(radius!=0&&mlat!=0.0&&mlon!=0.0) {
                                       isNew=false;
                                        index=1;
                                        Alat[index]=mlat;Alon[index]=mlon;
                                        index++;
                                        if(isNetConnected)final_addr=title;
                                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        //rl.setClickable(false);
                                        //rl.setVisibility(View.GONE);
                                        //recyclerView.setClickable(true);
                                        SetTimeClass data = mList.get(PrevPosition);
                                        data.setSelected(title);
                                        mList.set(PrevPosition, data);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                    else {
                                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }

                                }

                            });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                findViewById(R.id.map_layout).setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                //rl.setClickable(false);
                                //rl.setVisibility(View.GONE);
                            }
                        });
                        break;
                    /*case 4:
                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                        findViewById(R.id.map_layout).setClickable(false);
                        showDialog(FLOOR_DIALOG_ID);
                        break;*/
                    case 4:
                        otherStuff.setVisibility(View.VISIBLE);
                        repeat_list.setVisibility(View.VISIBLE);
                        findViewById(R.id.map_layout).setVisibility(View.GONE);
                        findViewById(R.id.map_layout).setClickable(false);
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_multiple_choice,customModes){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent){
                                // Get the Item from ListView
                                View view = super.getView(position, convertView, parent);

                                // Initialize a TextView for ListView each Item
                                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                                // Set the text color of TextView (ListView Item)
                                tv.setTextColor(colorList);

                                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
                                    animation.setStartOffset(position*500);
                                    view.startAnimation(animation);

                                // Generate ListView Item using TextView
                                return view;
                            }
                        };

                        repeat_list.setAdapter(adapter);
                        repeat_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        for(int j=0;j<4;j++)tm[j]=0;
                        repeat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //i=0;
                                //for(int i=0;i<50;i++)positions[i]=-1;
                                if(tm[position%4]==0)tm[position%4]=1;
                                else tm[position%4]=0;

                            }
                        });
                        Button save=(Button)findViewById(R.id.custom_ok_gps);
                        Button cancel=(Button)findViewById(R.id.custom_cancel_gps);
                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String change_title="";
                                modes=tm;
                                for(int k=0;k<4;k++){if(modes[k]==1){change_title+=customModes[k]+" ";}}
                                SetTimeClass lab = mList.get(PrevPosition);
                                lab.setSelected(change_title);
                                mList.set(PrevPosition, lab);
                                mAdapter.notifyDataSetChanged();
                                otherStuff.setVisibility(View.GONE);
                                repeat_list.setVisibility(View.GONE);
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                otherStuff.setVisibility(View.GONE);
                                repeat_list.setVisibility(View.GONE);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        AddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (final_addr == "" && final_lat == "" && final_label == "" && final_radius == "" && final_lon == "") {
                    Toast.makeText(mContext, "All Fields not Selected", Toast.LENGTH_SHORT).show();


                }
                if(modes[0]==1&&modes[1]==1||modes[2]==1&&modes[3]==1){
                    Toast.makeText(getApplicationContext(), "Selected Modes Are Illegal", Toast.LENGTH_SHORT).show();
                }
                else {
                    mode="";
                    for (int i = 0; i < 4; i++) {
                        mode+=modes[i];
                    }
                    if(mode.equals("0000")||mode.equals("")){mode="1000";}
                    mode=mode+" ";
                    if (IData.size() == 0) {
                        boolean check = false;
                        int j = 1;
                        String s = "", s2 = "";
                        if (radius == 0) {
                            index--;
                        }
                        for (j = 1; j < index; j++) {
                            if (j < index) {
                                s += Alon[j] + "//_//";

                            }
                            if (j < index) {
                                s2 += Alat[j] + "//_//";
                            }
                        }
                        final_lat = s;
                        final_lon = s2;
                        dbHelper.CreateGpsEvent(final_label, final_lat, final_lon, final_addr, final_radius, mode, final_status);


                        Intent myIntent = new Intent(gps.this, GpsBroadcastReciever.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(gps.this, 0, myIntent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                        finish();
                        Intent i = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(i);
                    } else if (IData.size() != 0) {
                        boolean check = false;
                        int j = 1;
                        do {
                            check = false;
                            if (j < index) {
                                final_lon += Alon[j] + "//_//";
                                check = true;
                            }
                            if (j < index) {
                                final_lat += Alat[j] + "//_//";
                                check = true;
                            }
                            j++;
                        } while (check);
                        long id = Long.parseLong(IData.get(6));
                        dbHelper.update_gps(id, final_label, final_lat, final_lon, final_addr, final_radius, mode, final_status);

                        Intent myIntent = new Intent(gps.this, GpsBroadcastReciever.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(gps.this, 0, myIntent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

                        finish();
                        Intent i = new Intent(mContext, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(i);
                    }

                }
            }
        });
        CancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(mContext, MainActivity.class);
                mContext.startActivity(i);
            }
        });

        e.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String Locat = e.getText().toString();
                    Toast.makeText(mContext, "Searching :" + Locat, Toast.LENGTH_SHORT).show();
                    if(isNetConnected) {
                        Geocoder geocoder;
                        List<Address> list;
                        try {
                            geocoder = new Geocoder(mContext);
                            list = geocoder.getFromLocationName(Locat, 1);
                            Address add = list.get(0);
                            if (add != null) {
                                getAddress(add.getLatitude(), add.getLongitude());
                            }
                            if (radius != 0) {
                                if (circle != null) {
                                    mMap.clear();
                                    circle.remove();
                                    circle.setVisible(false);
                                }
                                Circle c = mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(add.getLatitude(), add.getLongitude()))
                                        .radius(radius)
                                        .strokeColor(Color.RED)
                                        .strokeWidth(2)
                                        .fillColor(0x4DFF0000));
                                circle = c;
                            }
                        } catch (IOException e) {
                        }
                    }else{
                        Toast.makeText(mContext,"You Are Offline",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }else{
                    Toast.makeText(mContext,"No Result Found",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        });

    }



    public void getData() {
        if (IData.size() == 0) {
            SetTimeClass data = new SetTimeClass("Title", "Location");
            mList.add(data);

            data = new SetTimeClass("Radius(in meter)", "0");
            mList.add(data);

            data = new SetTimeClass("No. of Coordinates", "1");
            mList.add(data);

            data = new SetTimeClass("Add Location", " ");
            mList.add(data);

            data=new SetTimeClass("Mode","Silent");
            mList.add(data);

            mAdapter.notifyDataSetChanged();
        } else {
            SetTimeClass data = new SetTimeClass("Title", IData.get(0));
            mList.add(data);

            data = new SetTimeClass("Radius(in meter)", IData.get(5));
            mList.add(data);

            data = new SetTimeClass("No. Of Coordinates", "1");
            mList.add(data);

            data = new SetTimeClass("Add Location", IData.get(3));
            mList.add(data);

            String t1="";
            for(int i=0;i<4;i++){if(modes[i]==1)t1+=customModes[i]+" ";}
            data=new SetTimeClass("Mode",t1);
            mList.add(data);

            mAdapter.notifyDataSetChanged();

        }
    }

    public void getAddress(final double lat, final double lon) {
        if(isNetConnected) {
            List<Address> lv = new ArrayList<Address>();
            int max = 1;
            LatLng latLng = new LatLng(lat, lon);
            mlat=lat;mlon=lon;
            Alat[index] = lat;
            Alat[index] = lon;
            try {
                lv = geo.getFromLocation(lat, lon, max);
            } catch (IOException e) {
                Log.i("TAG", "Location Changed: ");
                e.printStackTrace();
            }
            android.location.Address addr = lv.get(0);

            StringBuilder str = new StringBuilder();
            if (addr != null) {
                for (int j = 0; j <= addr.getMaxAddressLineIndex(); j++) {
                    str.append(addr.getAddressLine(j));

                    title = str.toString();
                }
            }

            if (tempMarker != null) {
                tempMarker.remove();
            }

        /*if(isEditMode){
            finalMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(addr.getPremises()).snippet(title).visible(true));
            finalMarker.showInfoWindow();
        }
        else {*/

            tempMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Clicked Position").snippet(title).visible(true));
            if (!tempMarker.isInfoWindowShown()) tempMarker.showInfoWindow();

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }
    }
    /*public void getAlt(double lat,double lon){


        if(isNetConnected) {
            List<Address> lv = new ArrayList<Address>();
            int max = 1;
            LatLng latLng = new LatLng(lat, lon);
            try {
                lv = geo.getFromLocation(lat, lon, max);
            } catch (IOException e) {
                Log.i("TAG", "Location Changed: ");
                e.printStackTrace();
            }
            android.location.Address addr = lv.get(0);

            StringBuilder str = new StringBuilder();
            String Address="";
            if (addr != null) {
                for (int j = 0; j <= addr.getMaxAddressLineIndex(); j++) {
                    str.append(addr.getAddressLine(j));

                    Address = str.toString();
                }
                //Toast.makeText(mContext,title, Toast.LENGTH_LONG).show();
            }
            try {
                GoogleGeoInfo geoInfo = new GoogleGeoInfo() {
                    @Override
                    public void onGeocodeSuccess(PlaceResult places) {
                        mPlace = places.getResults()[0];
                        mLocation = mPlace.getGeometry().getLocation();
                        getElevationInfo(mLocation);
                        //Toast.makeText(mContext, "Geo Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGeocodeFailure(String status) {
                        //Toast.makeText(mContext, "Geo Fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onElevationSuccess(ElevationResult elevation) {
                        mAltitude=elevation.getResults()[0].getElevation().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        alti[hola++]=mAltitude;
                        //Toast.makeText(mContext, "Elevation Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onElevationFailure(String status) {
                        //Toast.makeText(mContext, "Elevation fail", Toast.LENGTH_SHORT).show();
                    }
                };
                geoInfo.getGeocodeInfo(Address);
            }catch (Exception e){}


        }
    }*/
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        dialog = new Dialog(gps.this);
        dialog.setContentView(R.layout.label_dialogue);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dismissDialog(LABEL_DIALOG_ID);

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub

            }
        });
        //final LinearLayout lm=(LinearLayout)findViewById(R.id.checkboxLayout);


        //lm.setVisibility(View.GONE);
        //CurrUnit=unitPicker.getValue();

        final EditText label = (EditText) dialog.findViewById(R.id.label_edit_text);
        label.setTextSize(16);
        Button save = (Button) dialog.findViewById(R.id.label_save);
        Button cancel = (Button) dialog.findViewById(R.id.label_cancel);
        label.setText("");
        switch (id) {
            case LABEL_DIALOG_ID:
                dialog.setTitle("LABEL");
                label.setText("Location Silence");

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final_label = label.getText().toString();
                        SetTimeClass lab = mList.get(PrevPosition);
                        lab.setSelected(final_label);

                        mList.set(PrevPosition, lab);
                        mAdapter.notifyDataSetChanged();

                        dismissDialog(LABEL_DIALOG_ID);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog(LABEL_DIALOG_ID);
                    }
                });
                break;
            case COUNT_DIALOG_ID:
                dialog.setTitle("No Of Coordinates");
                label.setHint("This indicates no. of points on map to be selected");
                label.setInputType(InputType.TYPE_CLASS_NUMBER);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMap.clear();
                        sCount=0;index=1;final_lat="";final_lon="";
                        for(int i=0;i<Alat.length;i++){Alat[i]=0;Alon[i]=0;}
                        final_count = label.getText().toString();
                        SetTimeClass lab = mList.get(PrevPosition);
                        lab.setSelected(final_count + " ");
                        mList.set(PrevPosition, lab);
                        mAdapter.notifyDataSetChanged();

                        dismissDialog(COUNT_DIALOG_ID);
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog(COUNT_DIALOG_ID);
                    }
                });
                break;
            case RADIUS_DIALOG_ID:
                dialog.setTitle("Radius");
                //lm.setVisibility(View.VISIBLE);
                label.setHint("Enter Radius(Optional)");
                label.setInputType(InputType.TYPE_CLASS_NUMBER);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final_radius = label.getText().toString();
                        radius=Integer.parseInt(final_radius);
                        SetTimeClass lab = mList.get(PrevPosition);
                        if (final_radius == "0") {
                            lab.setSelected("0m");
                        } else lab.setSelected(final_radius + "m");
                        mList.set(PrevPosition, lab);
                        mAdapter.notifyDataSetChanged();
                        dismissDialog(RADIUS_DIALOG_ID);
                       // lm.setVisibility(View.GONE);
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //lm.setVisibility(View.GONE);
                        dismissDialog(RADIUS_DIALOG_ID);
                    }
                });

        }
        return dialog;
    }

    private double Convertunit(int val,int unit){
        switch (unit){
            case 1:
                return val*1000;

            case 2:
                return val*100;

            case 3:
                return val*0.30480061;

            case 4:
                return val;

            case 5:
                return val/1000;

        }
        return 0;
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
// TODO Auto-generated method stub
        super.onPrepareDialog(id, dialog, bundle);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Connection_detector con = new Connection_detector(this);

        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                isNetConnected = con.isConnectingToInternet();
                if(!isNetConnected){Toast.makeText(mContext,"You Are Offline.",Toast.LENGTH_SHORT).show();}
                if(isNetConnected)geo = new Geocoder(this, Locale.getDefault());
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }else{

            isNetConnected = con.isConnectingToInternet();
            if(!isNetConnected){Toast.makeText(mContext,"You Are Offline.",Toast.LENGTH_SHORT).show();}
            if(isNetConnected)geo = new Geocoder(this, Locale.getDefault());
            mMap.setMyLocationEnabled(true);buildGoogleApiClient();}
        if(IData.size()==0){LatLng pccoe = new LatLng(18.651674, 73.761536);
        if(isNetConnected)getAddress(18.651674, 73.761536);
        tempMarker = googleMap.addMarker(new MarkerOptions().position(pccoe).title("pccoe").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).snippet("Pccoe"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pccoe));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings ui = mMap.getUiSettings();
        ui.setZoomControlsEnabled(true);
        ui.setMyLocationButtonEnabled(true);
        ui.setIndoorLevelPickerEnabled(true);
        ui.setZoomGesturesEnabled(true);
        ui.setCompassEnabled(true);
        ui.setScrollGesturesEnabled(true);
        ui.setAllGesturesEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                lc = mMap.getMyLocation();
                if (lc != null&&(isNetConnected)) {
                    getAddress(lc.getLatitude(),lc.getLongitude());
                   // if(mAltitude==0.0){Toast.makeText(mContext, "Cannot Get Elevation", Toast.LENGTH_LONG).show();}
                    return true;
                } else if(lc==null){
                    Toast.makeText(mContext, "Cannot Get Current Location", Toast.LENGTH_LONG).show();
                    return false;
                }else return false;
            }
        });


        /*LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        isLocationEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);*/

       /* if(!isNetConnected) {
            GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
                @Override
                public void onNmeaReceived(long timestamp, String nmea) {
                    nmeaFound = true;
                    // check that this is an RMC string
                    if (nmea.startsWith(NMEA_GGA)) {
                        Toast.makeText(mContext,"NMEA ELEVATION",Toast.LENGTH_SHORT).show();
                        String[] tokens = nmea.split(",");
                        try {
                            // get orthometric elevation
                            String elevation = tokens[altitude_element_id];
                            if (!elevation.equals("")) {

                                GGA_ALTITUDE = Double.parseDouble(elevation);
                                mAltitude=GGA_ALTITUDE;

                            }
                        } catch (SecurityException ex) {
                            Log.e("NMEA", "onNmeaReceived: "
                                    + ex.getMessage());

                            //onNmeaException(ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }
            };
        }
*/

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                //googleMap.addMarker(new MarkerOptions().position(point));
                //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10),2000,null);
                //if(isEditMode&&finalMarker!=null){finalMarker.remove();final_altitude="";}
                if(isNew){mMap.clear();}
                if(isNetConnected&&!canExit&&radius==0&&point!=null){getAddress(point.latitude, point.longitude);
                    //if(mAltitude==0.0&&radius==0){Toast.makeText(mContext,"Cannot Get Elevation.Try Using Gps/MyLocation",Toast.LENGTH_SHORT).show();}
                }
                else if(canExit){Toast.makeText(mContext,"Press OK To Save",Toast.LENGTH_SHORT).show();}
                else if(radius!=0){
                    if(isNetConnected&&point!=null){getAddress(point.latitude, point.longitude);}
                        if(circle!=null) {
                            circle.remove();
                            circle.setVisible(false);}
                            Circle c = mMap.addCircle(new CircleOptions()
                                    .center(point)
                                    .radius(radius)
                                    .strokeColor(Color.RED)
                                    .strokeWidth(2)
                                    .fillColor(0x4DFF0000));
                            circle = c;

                }
                else{
                    Toast.makeText(mContext,"You Are Offline",Toast.LENGTH_SHORT).show();}



            }
        });

       /* if(isLocationEnabled) {
            android.location.LocationListener locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //if(isNetConnected)getAddress(location.getLatitude(), location.getLongitude());

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    isLocationEnabled = false;
                    Toast.makeText(mContext, "Location Disabled", Toast.LENGTH_SHORT).show();
                }
            };
            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Location Update Error", Toast.LENGTH_SHORT).show();
            }
            }
            */




    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if(location!=null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(!isNetConnected){

                mlat = location.getLatitude();
                mlon = location.getLongitude();
            }
            if(radius!=0) {
                if (circle != null && !isNetConnected) {
                    circle.remove();
                    circle.setVisible(false);
                }
                Circle c = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(mlat, mlon))
                        .radius(radius)
                        .strokeColor(Color.RED)
                        .strokeWidth(2)
                        .fillColor(0x4DFF0000));
                circle = c;
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION_COARSE = 777;
    public static final int MY_PERMISSIONS_REQUEST_INTERNET = 2722;
    public static final int MY_PERMISSIONS_REQUEST_NETWORK_STATE = 88888;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else  if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION_COARSE);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION_COARSE);
            }
            return false;
        }
        else  if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_PERMISSIONS_REQUEST_NETWORK_STATE);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_NETWORK_STATE);
            }
            return false;
        }
        else  if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_INTERNET);
            }
            return false;
        }
        else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                        isLocationEnabled=true;
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION_COARSE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                        isLocationEnabled=true;
                    }

                } else {
                        isLocationEnabled=false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Coarse Location permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_NETWORK_STATE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_NETWORK_STATE)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_INTERNET:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                       isNetConnected=true;
                    }

                } else {
                        isNetConnected=false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

   /* public double getAltitudeFromNet(double def, Location loc) {
        Log.v(TAG, "Looking up net altitude");
        double result = -100000.0;
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);

        //HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpContext localContext = new BasicHttpContext();
        String url = "http://maps.googleapis.com/maps/api/elevation/" + "xml?locations="
                + String.valueOf(loc.getLatitude()) + "," + String.valueOf(loc.getLongitude()) + "&sensor=true";
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = (double) (Double.parseDouble(value));

                }
                instream.close();
            }
        } catch (ClientProtocolException e) {
            Log.w(TAG, "Looking up net altitude ClientProtocolException", e);
        } catch (IOException e) {
            Log.w(TAG, "Looking up net altitude IOException", e);
        }

        Log.i(TAG, "got net altitude " + (int) result);
        if (result > -1000) {
            return result;
        } else {
            return def;
        }
    }
   /* private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {

                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }


        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            mMap.clear();

            for (int i = 0; i < addresses.size(); i++) {

                Address address = (Address) addresses.get(i);
                Address addres = (Address) addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mlat = address.getLatitude();
                mlon = address.getLongitude();
                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addres.getLocality());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                markerOptions.snippet(addressText);

                mMap.addMarker(markerOptions);

                if (i == 0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            }
        }

    }
    */





  /* private double getElevationFromGoogleMaps(double longitude, double latitude) {
        double result = Double.NaN;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "http://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(latitude)
                + "," + String.valueOf(longitude)
                + "&sensor=true";
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = (double)(Double.parseDouble(value)*3.2808399); // convert from meters to feet
                }
                instream.close();
            }
        } catch (ClientProtocolException e) {}
        catch (IOException e) {}

        return result;
    }*/

    // Check for Geolocation information first

}

