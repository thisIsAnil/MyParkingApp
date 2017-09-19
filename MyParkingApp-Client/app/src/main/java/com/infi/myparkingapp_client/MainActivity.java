
package com.infi.myparkingapp_client;

import android.content.Context;
import android.content.Intent;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Switch;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private DrawerLayout mDrawerLayout;
    //ImageButton gps,timer;
        boolean doubleBackToExitPressedOnce =false;
    int count=0;
    Switch tmp;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 1000);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        setContentView(R.layout.activity_main);
        else
        setContentView(R.layout.activity_main_below_l);

        Toolbar tbr = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tbr);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        final Context mContext=this;

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {

            setupDrawerContent(navigationView);
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        /*tmp=(Switch)findViewById(R.id.switch_btn);
        tmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(tmp.isChecked()){
                   //TODO change db status
               }
            }
        });*/
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_selected},
                new int[] {-android.R.attr.state_selected},
                new int[] { android.R.attr.state_middle},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[] {
                Color.WHITE,
                Color.parseColor("#e91e63"),
                Color.parseColor("#ec407a"),
                Color.parseColor("#e91e63")
        };
        /*final TextView t1,t2;t1=(TextView)findViewById(R.id.frame_t1);t2=(TextView)findViewById(R.id.frame_t2);
        gps=(ImageButton) findViewById(R.id.gps);
        timer=(ImageButton) findViewById(R.id.timer);*/
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // gps.setVisibility(View.GONE);
                //timer.setVisibility(View.GONE);
                //t1.setVisibility(View.GONE);
               // t2.setVisibility(View.GONE);
                int x=(int)v.getX();
                int y=(int)v.getY();
                animateFAB();
                Intent a_gps=new Intent(mContext, gps.class);
                a_gps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                a_gps.putExtra("getX",x);
                a_gps.putExtra("getY",y);
                a_gps.putStringArrayListExtra("data",new ArrayList<String>());
                startActivity(a_gps);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateFAB();
                int x=(int)v.getX();
                int y=(int)v.getY();
                Intent a_timer = new Intent(mContext, Timer.class);
                a_timer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                a_timer.putExtra("getX",x);
                a_timer.putExtra("getY",y);
                a_timer.putStringArrayListExtra("data",new ArrayList<String>());
                startActivity(a_timer);
            }
        });

        final ColorStateList colorStateList=new ColorStateList(states,colors);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fab.setBackgroundTintList(colorStateList);
                animateFAB();

            }
        });


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
       tabLayout.setupWithViewPager(viewPager);
    }
    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;


        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
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
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new MainListFragment(), "TIMER");
        adapter.addFragment(new Gps_list_Fragment(), "GPS");
       viewPager.setAdapter(adapter);
    }
    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        //startActivity(new Intent(MainActivity.this,MainActivity.class));
                        return true;
                    case R.id.nav_messages:
                            startActivity(new Intent(MainActivity.this,WiFiDirectActivity.class));
                        return true;
                }
                return false;

            }
        });

    }

    @Override
    public void onClick(View view) {
        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;


        }
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
