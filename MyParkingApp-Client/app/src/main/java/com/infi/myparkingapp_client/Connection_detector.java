package com.infi.myparkingapp_client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Infi on 3/21/2016.
 */
public class Connection_detector {
    public Context _context;

    public Connection_detector(Context context){
        this._context=context;
    }
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
