package com.infi.myparkingapp_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

/**
 * Created by INFIi on 6/24/2016.
 */
public class SilentBroadCastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            long[] data = intent.getLongArrayExtra("Stop");
            Intent i = new Intent(context, SilentIntent.class);
            i.putExtra("Diff", data);
            context.startService(i);

    }
}
