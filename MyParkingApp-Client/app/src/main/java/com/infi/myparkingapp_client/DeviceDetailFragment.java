package com.infi.myparkingapp_client;
/**
 * Created by Asus on 17-09-2016.
 */
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true

                );

                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);
            }
        });
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });
        return mContentView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String val=data.getStringExtra("empty_slot");
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + val);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, val);
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);
        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));
        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Owner IP - " + info.groupOwnerAddress.getHostAddress());
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                    .execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
           // mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }
        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }
    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
    }
    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        this.getView().setVisibility(View.GONE);
    }
    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private TextView statusText;
        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }
        @Override
        protected String doInBackground(Void... params) {
            ServerSocket serverSocket = null;
            Socket client = null;
            DataInputStream inputstream = null;
            try {
                serverSocket = new ServerSocket(8988);
                client = serverSocket.accept();
                inputstream = new DataInputStream(client.getInputStream());
                String str = inputstream.readUTF();
                serverSocket.close();
                return str;
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }finally{
                if(inputstream != null){
                    try{
                        inputstream.close();
                    } catch (IOException e) {
                        Log.e(WiFiDirectActivity.TAG, e.getMessage());
                    }
                }
                if(client != null){
                    try{
                        client.close();
                    } catch (IOException e) {
                        Log.e(WiFiDirectActivity.TAG, e.getMessage());
                    }
                }
                if(serverSocket != null){
                    try{
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.e(WiFiDirectActivity.TAG, e.getMessage());
                    }
                }
            }
        }
        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("Empty Slot is - " + result);
                Toast.makeText(context,"Received Success: "+result,Toast.LENGTH_LONG).show();
                if(result.equals("-1"+"//_//"+"-1"+"//_//"+"-1")){
                    context.startActivity(new Intent(context,AccessDeniedActivity.class));
                }else{
                    String[] data=result.split("//_//");
                    StringPref stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.ALLOCATED_SLOT,data[2]);
                    stringPref.setStringPref(result.split("//_//")[2]);
                    stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.SERVER_IP,data[0]);
                    stringPref.setStringPref(data[0]);
                    stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.EMPTY_SLOTS,data[1]);
                    stringPref.setStringPref(data[1]);
                    stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.SLOTS_NO,data[3]);
                    stringPref.setStringPref(data[3]);
                    stringPref=new StringPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.LANES,data[4]);
                    stringPref.setStringPref(data[4]);
                    IntPref intPref=new IntPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.ROAD_WIDTH,Integer.parseInt(data[5]));
                    intPref.set(Integer.parseInt(data[5]));
                    intPref=new IntPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.LANE_WIDTH,Integer.parseInt(data[6]));
                    intPref.set(Integer.parseInt(data[6]));
                    intPref=new IntPref(context.getSharedPreferences(KEYS.PREF_NAME,Context.MODE_PRIVATE),KEYS.SLOT_WIDTH,Integer.parseInt(data[7]));
                    intPref.set(Integer.parseInt(data[7]));
                    Intent i=new Intent(context,SimulationActivity.class);
                    i.putExtra("empty_slots",result);
                    context.startActivity(i);
                }
                //TODO Generate Route
            }
        }


        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }
    }


    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }
}
