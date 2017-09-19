package com.infi.myparkingapp_client;



import android.app.AlarmManager;
import android.app.Dialog;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.List;

public class Timer  extends AppCompatActivity  {


    private final static int MSG_UPDATE_TIME = 0;

    private List<SetTimeClass> mList = new ArrayList<>();
    ArrayList<String> arrayList;
    private RecyclerView recyclerView;
    private TimerListAdapter mAdapter;
    private ListView repeat_list;
    int[] day=new int[7];
    //static final int TIME_DIALOG_ID=1111;
    //static final int REPEAT_DIALOG_ID=1010;
    //static final int CUSTOM_DAYS_ID=9999;
    static final int LABEL_DIALOG_ID=7777;
    int hour,minutee;
    String final_sHr,final_sMin,final_eHr,final_eMin,final_sAm,final_eAm;
    String hr,min,am_pm;
    Button save,cancel;
    String final_days;
    TimerAdapter dbHelper;
    String final_title;
    int[] positions=new int[50];int i=0;
    String isDaily;
    String mode;
    int[] modes=new int[4];
    int[] tmp=new int[4];
    int getPostition;
    TimePicker timePicker;
    ColorStateList colorList;
    String change_title="";
    EditText label;String LabelText;
    Context ctx;
    int final_status;
    long id;

    RelativeLayout otherStuff,dialogue_stuff;
    String[] contents=new String[]{
      "Once","daily","Mon to Fri","Custom"
    };
    String[] customModes=new String[]{
      "Silent","Normal","WiFi On","WiFi Off"
    };
    String[] customDays=new String[]{
            "Sunday", "Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        FrameLayout rootLayout=(FrameLayout)findViewById(R.id.timer_activity_frame);
        final Intent intent=getIntent();
        int w=intent.getIntExtra("getX",0);
        int h=intent.getIntExtra("getY",0);
        arrayList=intent.getStringArrayListExtra("data");
        //if(arrayList.size()!=0)Toast.makeText(Timer.this, "Entered In edit", Toast.LENGTH_SHORT).show();
        Toolbar toolbar=(Toolbar)findViewById(R.id.timer_actionBar);
        toolbar.setTitle("Timer");
        setSupportActionBar(toolbar);


        ((TextView)findViewById(R.id.bar_title)).setText("Timer");
        final ActionBar ab=getSupportActionBar();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

            if (savedInstanceState == null) {
                CircularReveal cr;
                if(w==0||h==0)cr=new CircularReveal(rootLayout.getRootView());
                else cr=new CircularReveal(rootLayout.getRootView(),w,h);
                cr.StartCircularReveal();

            }
        }else{
            Animation cr= AnimationUtils.loadAnimation(Timer.this,R.anim.activity_open);
            rootLayout.startAnimation(cr);
        }

        dbHelper=new TimerAdapter(this);
        try{dbHelper.open();}catch (Exception e){
            Log.e("TAG","Cannot Open Db");
            e.printStackTrace();
        }
        timePicker=(TimePicker)findViewById(R.id.timePicker);
        repeat_list=(ListView)findViewById(R.id.repeatList);
        mAdapter = new TimerListAdapter(this,mList);
        //custom_list=(ListView)findViewById(R.id.customdialoglist);
        ctx=getApplicationContext();

        if(arrayList.size()==0){
            hour=0;minutee=0;am_pm="Am";LabelText="Label";
            for(int i=0;i<50;i++)positions[i]=-1;
            for(int i=0;i<7;i++){day[i]=0;if(i<4){modes[i]=0;}}
            modes[0]=1;
        final_days="";final_eHr="";final_eMin="";final_title="Timer";
            mode="";
        final_eAm="";final_sAm="";final_sMin="";final_sHr="";isDaily="Y";final_status=1;}
        else{
            final_days=arrayList.get(3);
            isDaily="Y";
            if(arrayList.get(5)=="Active")final_status=1;
            else final_status=0;
            final_title=arrayList.get(0);
            id=Long.parseLong(arrayList.get(6));
            mode=arrayList.get(7);
            char[] start,stop;
            /*start=arrayList.get(1).toCharArray();
            stop=arrayList.get(2).toCharArray();
            if(start.length==6){
            final_sHr=""+start[0]+"";
            final_sMin=""+start[1]+start[2]+"";
            final_sAm=""+start[4]+start[5]+"";}
            else{
                if(start.length==7){
                    final_sHr=""+start[0]+start[1]+"";
                    final_sMin=""+start[2]+start[3]+"";
                    final_sAm=""+start[5]+start[6]+"";}
            }
            if(stop.length==6) {
                final_eHr = "" + stop[0] + "";
                final_eMin = "" + stop[1] + stop[2] + "";
                final_eAm = "" + stop[4] + stop[5] + "";
            }else{
                if(stop.length==7){
                    final_eHr = "" + stop[0] +stop[1]+ "";
                    final_eMin = "" + stop[2] + stop[3] + "";
                    final_eAm = "" + stop[5] + stop[6] + "";
                }
            }*/
            final_sHr=arrayList.get(8);
            final_sMin=arrayList.get(9);
            final_eHr=arrayList.get(10);
            final_eMin=arrayList.get(11);
            char[] chars=final_days.toCharArray();
            char[] c2=mode.toCharArray();
            for(int i=0;i<7;i++){
                if(chars[i]=='1')day[i]=1;
                else day[i]=0;

                if(i<4){
                    if(c2[i]=='1')modes[i]=1;
                    else modes[i]=0;
                }
            }

        }

        //repeat_list=(ListView)findViewById(R.id.repeatList);
        otherStuff=(RelativeLayout)findViewById(R.id.rl_other_stuff);
        dialogue_stuff=(RelativeLayout)findViewById(R.id.dialogue_layout);
       recyclerView=(RecyclerView)findViewById(R.id.set_timer_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
       recyclerView.addItemDecoration(new ListDivider(this, LinearLayoutManager.VERTICAL));
        final int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked},
                new int[] {-android.R.attr.state_enabled},
                new int[] { android.R.attr.state_pressed},
                new int[]{}
        };

        final int[] colors = new int[] {
                Color.RED,
                Color.BLUE,
                Color.RED,
                Color.DKGRAY,
        };
            colorList=new ColorStateList(states,colors);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        final Context cnt=getApplicationContext();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(cnt, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
            getPostition=position;

                TextView tv=(TextView)findViewById(R.id.remaining_text);
                tv.setVisibility(View.GONE);
                if(position==0){
                    repeat_list.setVisibility(View.GONE);
                    otherStuff.setVisibility(View.GONE);
                        showDialog(LABEL_DIALOG_ID);
                }
               else if(position==1){
                    repeat_list.setVisibility(View.VISIBLE);
                    otherStuff.setVisibility(View.GONE);
                    //timePicker.setVisibility(View.GONE);
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_single_choice,contents){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        // Get the Item from ListView
                        View view = super.getView(position, convertView, parent);

                        // Initialize a TextView for ListView each Item
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);

                        // Set the text color of TextView (ListView Item)
                        tv.setTextColor(colorList);

                        // Generate ListView Item using TextView
                        return view;
                    }
                };

                repeat_list.setAdapter(adapter);
                    repeat_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    repeat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            i=0;
                            for(int i=0;i<50;i++)positions[i]=-1;
                            for(int i=0;i<7;i++){day[i]=0;}
                            switch (position) {
                                case 0:
                                    repeat_list.setVisibility(View.VISIBLE);
                                    Calendar cal = Calendar.getInstance();
                                    final_days="";
                                    isDaily = "N";
                                    switch (cal.get(Calendar.DAY_OF_WEEK)) {
                                        case Calendar.MONDAY:
                                            //final_days = "M";
                                            day[1]=1;
                                           // change_title="Mon";
                                            break;
                                        case Calendar.TUESDAY:
                                            //final_days = "T";
                                            day[2]=1;
                                            //change_title="Tue";
                                            break;
                                        case Calendar.WEDNESDAY:
                                            //final_days = "W";
                                            day[3]=1;
                                            //change_title="Wed";
                                            break;
                                        case Calendar.THURSDAY:
                                            //final_days = "TH";
                                            day[4]=1;
                                            //change_title="Thu";
                                            break;
                                        case Calendar.FRIDAY:
                                            day[5]=1;
                                            //final_days = "F ";
                                            //change_title="Fri";
                                            break;
                                        case Calendar.SATURDAY:
                                            //final_days = "S ";
                                            day[6]=1;
                                            //change_title="Sat";
                                            break;
                                        case Calendar.SUNDAY:
                                                //final_days = "SU ";
                                            day[0]=1;
                                           // change_title="Sun";
                                    }
                                    repeat_list.setVisibility(View.GONE);
                                    timePicker.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    repeat_list.setVisibility(View.VISIBLE);
                                    isDaily = "Y";
                                    //final_days = "SU,M,T,W,TH,F,S";
                                    for(int i=0;i<7;i++){day[i]=1;}
                                    //change_title="Sun,Mon,tue,Wed,Thu,Fri,Sat";
                                    repeat_list.setVisibility(View.GONE);
                                    break;
                                case 2:
                                    repeat_list.setVisibility(View.VISIBLE);
                                    //final_days = "M,T,W,TH,F";
                                    isDaily = "Y";
                                    day[0]=0;
                                    for(int i=1;i<6;i++){day[i]=1;}
                                    //change_title="Mon,Tue,Wed,Thu,Fri,Sat";
                                    repeat_list.setVisibility(View.GONE);
                                    break;
                                case 3:
                                    repeat_list.setVisibility(View.VISIBLE);
                                    dialogue_stuff.setVisibility(View.VISIBLE);
                                    isDaily = "Y";
                                    repeat_list.removeAllViewsInLayout();
                                    ArrayAdapter<String> CAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_multiple_choice,customDays){
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent){
                                    // Get the Item from ListView
                                    final View view = super.getView(position, convertView, parent);

                                    // Initialize a TextView for ListView each Item
                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                        tv.setTextColor(colorList);
                                    // Set the text color of TextView (ListView Item)
                                    //tv.setTextColor(Color.DKGRAY);

                                    // Generate ListView Item using TextView
                                    return view;
                                         }
                                    };
                                    save=(Button)findViewById(R.id.custom_ok);
                                    cancel=(Button)findViewById(R.id.custom_cancel);
                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            repeat_list.setVisibility(View.GONE);
                                            otherStuff.setVisibility(View.GONE);
                                            cancel.setVisibility(View.GONE);
                                            save.setVisibility(View.GONE);
                                            dialogue_stuff.setVisibility(View.GONE);

                                        }
                                    });
                                    save.setVisibility(View.VISIBLE);cancel.setVisibility(View.VISIBLE);
                                    save.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //Toast.makeText(ctx,"On save"+day[0], Toast.LENGTH_SHORT).show();
                                            change_title="";
                                            for(int k=0;k<positions.length;k++){
                                                //Toast.makeText(v.getContext(),"On save "+day[0], Toast.LENGTH_SHORT).show();
                                                    //if(day[i]==1){change_title+=customDays[i]+" ";}
                                                    if (positions[k] != -1 )
                                                    {day[positions[k]]=1-day[positions[k]];}
                                                //Toast.makeText(ctx,"On save"+day[0]+" "+k, Toast.LENGTH_SHORT).show();}
                                            }
                                            repeat_list.setVisibility(View.GONE);
                                            otherStuff.setVisibility(View.GONE);
                                            cancel.setVisibility(View.GONE);
                                            save.setVisibility(View.GONE);
                                            dialogue_stuff.setVisibility(View.GONE);
                                            change_title="";
                                            for(int k=0;k<7;k++){if(day[k]==1){change_title+=customDays[k]+" ";}}
                                            SetTimeClass time=mList.get(getPostition);
                                            //Toast.makeText(ctx,change_title+" "+getPostition+""+day[0],Toast.LENGTH_SHORT).show();
                                            time.setSelected(change_title);
                                            mList.set(getPostition,time);
                                            mAdapter.notifyDataSetChanged();

                                        }
                                    });
                            repeat_list.setAdapter(CAdapter);
                                    repeat_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                    isDaily = "Y";
                                    //for (int j=0;j<7;j++){positions[j]=-1;}
                                    repeat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            positions[i] = position;
                                            i++;
                                            //Toast.makeText(ctx, i + "here"+position, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            }
                            change_title="";
                            for(int k=0;k<7;k++){if(day[k]==1){change_title+=customDays[k]+" ";}}
                            SetTimeClass time=mList.get(getPostition);
                            //Toast.makeText(ctx,change_title+" "+getPostition+""+day[0],Toast.LENGTH_SHORT).show();
                            time.setSelected(change_title);
                            mList.set(getPostition,time);
                            mAdapter.notifyDataSetChanged();

                        }
                    });

                }
                else if(position==2){
                    repeat_list.setVisibility(View.GONE);
                    otherStuff.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    dialogue_stuff.setVisibility(View.VISIBLE);
                   // custom_list.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    final TextView textView=(TextView)findViewById(R.id.remaining_text);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(" ");
                    timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                            hour=hourOfDay;
                            minutee=minute;
                            updateTime(hour,minutee);
                        }
                    });
                    save=(Button)findViewById(R.id.custom_ok);
                    cancel=(Button)findViewById(R.id.custom_cancel);
                    save.setVisibility(View.VISIBLE);cancel.setVisibility(View.VISIBLE);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar c=Calendar.getInstance();
                            updateTime(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
                            otherStuff.setVisibility(View.GONE);
                            save.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                            dialogue_stuff.setVisibility(View.GONE);
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            otherStuff.setVisibility(View.GONE);
                            save.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                            dialogue_stuff.setVisibility(View.GONE);
                        }
                    });
                }
                else if(position==3){

                    repeat_list.setVisibility(View.GONE);
                    otherStuff.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    dialogue_stuff.setVisibility(View.VISIBLE);
                    // custom_list.setVisibility(View.GONE);
                    timePicker.setVisibility(View.VISIBLE);
                    final TextView textView=(TextView)findViewById(R.id.remaining_text);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(" ");
                    timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                            hour=hourOfDay;
                            minutee=minute;
                            updateTime(hour,minutee);
                        }
                    });
                    save=(Button)findViewById(R.id.custom_ok);
                    cancel=(Button)findViewById(R.id.custom_cancel);
                    save.setVisibility(View.VISIBLE);cancel.setVisibility(View.VISIBLE);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar c=Calendar.getInstance();
                            updateTime(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
                            otherStuff.setVisibility(View.GONE);
                            save.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                            dialogue_stuff.setVisibility(View.GONE);
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            otherStuff.setVisibility(View.GONE);
                            save.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);
                            dialogue_stuff.setVisibility(View.GONE);
                        }
                    });


                }
                else if(position==4){
                    repeat_list.setVisibility(View.VISIBLE);
                    dialogue_stuff.setVisibility(View.VISIBLE);
                    //otherStuff.setVisibility(View.GONE);
                    //timePicker.setVisibility(View.GONE);
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_multiple_choice,customModes){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent){
                            // Get the Item from ListView
                            View view = super.getView(position, convertView, parent);

                            // Initialize a TextView for ListView each Item
                            TextView tv = (TextView) view.findViewById(android.R.id.text1);

                            // Set the text color of TextView (ListView Item)
                            tv.setTextColor(colorList);
                            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.fade_in);
                            animation.setStartOffset(position*500);
                            view.startAnimation(animation);
                            // Generate ListView Item using TextView
                            return view;
                        }
                    };

                    repeat_list.setAdapter(adapter);
                    repeat_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    for(int j=0;j<4;j++)tmp[j]=0;
                    repeat_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //i=0;
                            //for(int i=0;i<50;i++)positions[i]=-1;
                            if(tmp[position%4]==0)tmp[position%4]=1;
                            else tmp[position%4]=0;

                        }
                    });
                    save=(Button)findViewById(R.id.custom_ok);
                    cancel=(Button)findViewById(R.id.custom_cancel);
                    save.setVisibility(View.VISIBLE);cancel.setVisibility(View.VISIBLE);
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            modes=tmp;
                            change_title=" ";
                            repeat_list.setVisibility(View.GONE);
                            otherStuff.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            save.setVisibility(View.GONE);
                            dialogue_stuff.setVisibility(View.GONE);
                            change_title="";
                            for(int k=0;k<4;k++){if(modes[k]==1){change_title+=customModes[k]+" ";}}
                            SetTimeClass time=mList.get(getPostition);
                            //Toast.makeText(ctx,change_title+" "+getPostition+""+day[0],Toast.LENGTH_SHORT).show();
                            time.setSelected(change_title);
                            mList.set(getPostition,time);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            repeat_list.setVisibility(View.GONE);
                            otherStuff.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            save.setVisibility(View.GONE);
                            dialogue_stuff.setVisibility(View.GONE);
                        }
                    });
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        Button save,cancel;
        save=(Button)findViewById(R.id.tb_save_btn);
        cancel=(Button)findViewById(R.id.tb_cancel_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (final_sHr.isEmpty() && final_sMin.isEmpty() && final_sAm.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "StartTime Not Selected", Toast.LENGTH_SHORT).show();
                }
                if (final_days.isEmpty() && final_eAm.isEmpty() && final_eHr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "StopTime Not Selected", Toast.LENGTH_SHORT).show();
                }
                if(modes[0]==1&&modes[1]==1||modes[2]==1&&modes[3]==1){
                    Toast.makeText(getApplicationContext(), "Selected Modes Are Illegal", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(final_sAm!=null&&final_eAm!=null){
                    final_eMin += "" + final_eAm;
                    final_sMin += "" + final_sAm;}
                    final_days = "";
                    mode="";
                    for (int i = 0; i < 7; i++) {
                        final_days += day[i];
                        if(i<4){mode+=modes[i];}
                    }
                    if(mode.equals("0000")||mode.equals("")){mode="1000";}
                    mode=mode+" ";
                    if(arrayList.size()==0){
                        long idd=dbHelper.createEvent(final_title, final_sHr, final_sMin, final_eHr, final_eMin, final_days,isDaily,mode, final_status);
                        Calendar c=Calendar.getInstance();
                        int t=1;
                        if(isDaily=="Y"){t=0;}
                        else t=1;

                        Intent myIntent = new Intent(Timer.this, TimerBroadCastReciever.class);
                        long[] content=new long[]{idd,t,0};
                        myIntent.putExtra("ID",content);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(Timer.this,0, myIntent,0);
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                        c.setTimeInMillis(System.currentTimeMillis());
                        alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pendingIntent);


                    }
                    else{


                       dbHelper.update(id,final_title, final_sHr, final_sMin, final_eHr, final_eMin, final_days,isDaily,mode, final_status);
                        if(final_status==1){Calendar c=Calendar.getInstance();
                            int t=1;
                            if(isDaily=="Y"){t=0;}else t=1;
                            Intent myIntent = new Intent(Timer.this, TimerBroadCastReciever.class);
                            long[] data=new long[]{id,t,0};
                            myIntent.putExtra("ID",data);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx,0, myIntent,0);

                            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                            c.setTimeInMillis(System.currentTimeMillis());
                            alarmManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pendingIntent);

                        }
                    }
                    finish();
                    Intent i = new Intent(ctx, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(ctx, MainActivity.class);

                startActivity(i);

            }
        });
        getData();

    }



    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog=null;
        switch (id) {
            case LABEL_DIALOG_ID:

                dialog = new Dialog(Timer.this);
                dialog.setContentView(R.layout.label_dialogue);
                dialog.setTitle("LABEL");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
// TODO Auto-generated method stub

                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
// TODO Auto-generated method stub

                    }
                });
                label=(EditText)dialog.findViewById(R.id.label_edit_text);
                label.setText("Timer");
                                save=(Button)dialog.findViewById(R.id.label_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LabelText=label.getText().toString();
                        SetTimeClass lab=mList.get(getPostition);
                        lab.setSelected(LabelText);
                        Toast.makeText(ctx,LabelText,Toast.LENGTH_SHORT).show();
                        mList.set(getPostition,lab);
                        mAdapter.notifyDataSetChanged();
                        final_title=LabelText;
                        dismissDialog(LABEL_DIALOG_ID);
                    }
                });
                cancel=(Button)dialog.findViewById(R.id.label_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog(LABEL_DIALOG_ID);
                    }
                });
        }

            return dialog;
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
// TODO Auto-generated method stub
        super.onPrepareDialog(id, dialog, bundle);

        switch(id) {
            case LABEL_DIALOG_ID:
//
                break;
        }



    }



  private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String sHr = new StringBuilder().append(hours).toString();
         String sMin = new StringBuilder().append(minutes).toString();
        String isAm=timeSet;
        hr=sHr;
        min=sMin;
        am_pm=isAm;
        if(getPostition==2){
            final_sHr=hr;
            final_sMin=min;
            final_sAm=am_pm;
            //Toast.makeText(getApplicationContext(),hr+":"+min,Toast.LENGTH_SHORT).show();
            SetTimeClass time=mList.get(getPostition);
            time.setSelected(final_sHr+":"+final_sMin+" "+am_pm);
            mList.set(getPostition,time);
            mAdapter.notifyDataSetChanged();
        }
        else if(getPostition==3){
            final_eHr=hr;
            final_eMin=min;
            final_eAm=am_pm;
           // Toast.makeText(getApplicationContext(),hr+":"+min,Toast.LENGTH_SHORT).show();
            SetTimeClass time=mList.get(getPostition);
            //String label=time.getLabel();
            SetTimeClass start_time=mList.get(2);
            time.setSelected(final_eHr+":"+final_eMin+" "+am_pm);
           TextView textView=(TextView)findViewById(R.id.remaining_text);
            textView.setVisibility(View.VISIBLE);
            textView.setTextSize(14);
            textView.setText("The Device Will Be Silenced From"+start_time.getSelected()+" To "+time.getSelected());
            mList.set(getPostition,time);
            mAdapter.notifyDataSetChanged();

        }
    }

    private void getData() {


        if(arrayList.size()==0) {
            SetTimeClass data = new SetTimeClass("Label", "Timer");
            mList.add(data);

            data = new SetTimeClass("Repeat", "Once");
            mList.add(data);

            final Calendar cal = Calendar.getInstance();
            int tmp = cal.get(Calendar.HOUR_OF_DAY);
            if (tmp > 12) tmp -= 12;
            String s="";
            int minute=cal.get(Calendar.MINUTE);
            if(minute<10){s="0"+minute;}
            else{s=minute+"";}
            hr = new StringBuilder().append((tmp)).toString();
            min = new StringBuilder().append(s).toString();
            //Toast.makeText(this, cal.get(Calendar.DAY_OF_WEEK)+" ", Toast.LENGTH_SHORT).show();
            day[cal.get(Calendar.DAY_OF_WEEK)-1]=1;
            String am_pm = "";
            if (cal.get(Calendar.AM_PM) == Calendar.AM) am_pm = "AM";
            else if (cal.get(Calendar.AM_PM) == Calendar.PM) am_pm = "PM";
            final_sHr=hr;
            final_sMin=min;final_sAm=am_pm;
            data = new SetTimeClass("Start Time: ", hr + ":" + min + am_pm);
            mList.add(data);

            data = new SetTimeClass("Stop TIme: ", " ");
            mList.add(data);

            data=new SetTimeClass("Mode"," Silent");
            mList.add(data);

            mAdapter.notifyDataSetChanged();
        }
        else{
            SetTimeClass data = new SetTimeClass("Label",arrayList.get(0));
            mList.add(data);
            String t2="";
            for(int i=0;i<4;i++){if(i<4&&modes[i]==1){
                t2+=customModes[i]+" ";
            }}
            if(arrayList.get(4)=="N")
            data = new SetTimeClass("Repeat","Once" );
            else
            {
                String tmp="";
                for(int k=0;k<7;k++){
                    if(day[k]==1){tmp+=customDays[k]+" ";}

                }
                data=new SetTimeClass("Repeat",tmp);}
            mList.add(data);

            data = new SetTimeClass("Start Time: ",arrayList.get(1));
            mList.add(data);

            data = new SetTimeClass("Stop TIme: ", arrayList.get(2));
            mList.add(data);
            //bit 0=silent 1=normal 2=wifi on 3 =wifi off


            data=new SetTimeClass("Mode:",t2);
            mList.add(data);
            //defaultList=mList;
            mAdapter.notifyDataSetChanged();

        }

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }




}

