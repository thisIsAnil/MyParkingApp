package com.example.asus.myparkingapp;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.asus.myparkingapp.FaceVerification.ui.DetectionActivity;
import com.example.asus.myparkingapp.FaceVerification.ui.FindSimilarFaceActivity;
import com.example.asus.myparkingapp.FaceVerification.ui.GroupingActivity;
import com.example.asus.myparkingapp.FaceVerification.ui.IdentificationActivity;
import com.example.asus.myparkingapp.FaceVerification.ui.VerificationMenuActivity;
import com.example.asus.myparkingapp.R;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        Toolbar tbr = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(tbr);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        final Context mContext=this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);
        if (navigationView != null) {

            setupDrawerContent(navigationView);
            navigationView.getMenu().getItem(2).setChecked(true);
        }
        if (getString(R.string.subscription_key).startsWith("Please")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
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

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(MainActivity.this,IdentificationActivity.class));
                        return true;
                    case R.id.nav_messages:
                        startActivity(new Intent(MainActivity.this,SettingActivity.class));
                        return true;
                    case R.id.more:
                        return true;
                    case R.id.camera:
                        startActivity(new Intent(MainActivity.this,CameraActivity.class));
                        break;
                }
                return false;

            }
        });

    }
    public void detection(View view) {
        Intent intent = new Intent(this, DetectionActivity.class);
        startActivity(intent);
    }

    public void verification(View view) {
        Intent intent = new Intent(this, VerificationMenuActivity.class);
        startActivity(intent);
    }

    public void grouping(View view) {
        Intent intent = new Intent(this, GroupingActivity.class);
        startActivity(intent);
    }

    public void findSimilarFace(View view) {
        Intent intent = new Intent(this, FindSimilarFaceActivity.class);
        startActivity(intent);
    }

    public void identification(View view) {
        Intent intent = new Intent(this, IdentificationActivity.class);
        startActivity(intent);
    }
}
