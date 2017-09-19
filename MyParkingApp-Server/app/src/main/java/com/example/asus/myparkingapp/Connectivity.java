package com.example.asus.myparkingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.net.InetAddress;

/**
 * Created by Asus on 18-09-2016.
 */
public class Connectivity {
    private Context context;
    public Connectivity(Context context){
        this.context=context;
    }

    public boolean CanConnect(){
        ConnectivityManager ConnectionManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
            //Toast.makeText(context, "Network Available", Toast.LENGTH_LONG).show();
            return true;

        }
        else
        {
            Toast.makeText(context, "Network Not Available", Toast.LENGTH_LONG).show();
            return false;

        }

    }
}
