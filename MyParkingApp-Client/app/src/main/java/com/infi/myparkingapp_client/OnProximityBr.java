package com.infi.myparkingapp_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

/**
 * Created by INFIi on 7/9/2016.
 */
public class OnProximityBr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //final String key_enter = LocationManager.KEY_PROXIMITY_ENTERING;
        //final Boolean entering = intent.getBooleanExtra(key_enter, false);
        //Toast.makeText(context,entering+" ",Toast.LENGTH_SHORT).show();
        //if(!entering) {}
           long[] data = intent.getLongArrayExtra("MODES");
            if (data != null) {
                if (data[0] == 1) {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                } else if (data[1] == 1) {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                if (data[2] == 1) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    if (!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
                } else if (data[3] == 1) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(false);
                }


        }
    }

}

