package com.infi.myparkingapp_client;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by INFIi on 9/20/2016.
 */
public class SimulationActivity extends Activity{
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        TextView slot=(TextView)findViewById(R.id.Allocated_slot);
        Intent i=getIntent();
        if(i!=null) {
             data= getIntent().getStringExtra("empty_slots");
            if (data != null){
                slot.setText(data.split("//_//")[2]);
                SimulateRoute(data);
            }

        }


    }

    public void SimulateRoute(final String data) {
        String[] strings = data.split("//_//");
        if (strings != null && strings.length >= 7 && !strings[0].equals("-1")) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.simulate_route);
            int button_size = Integer.parseInt(strings[3]);
            int lane_size = Integer.parseInt(strings[4]);
            int road_width = Integer.parseInt(strings[5]);
            final String empty_slots = strings[1];
            List<Integer> status = GenerateStatus(empty_slots);

            final int allocated = Integer.parseInt(strings[2]);
            LinearLayout lane = new LinearLayout(SimulationActivity.this);
            for (int i = 0; i < lane_size; i++) {
                lane = new LinearLayout(SimulationActivity.this);
                lane.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < button_size; j++) {
                    Button button = new Button(SimulationActivity.this);
                    final int index = i * (button_size - 1) + (j + 1);
                    button.setId(2000 * index);
                    button.setText(index + "");
                    int st = status.get(index);
                    if (st == 1) button.setBackgroundColor(Color.RED);
                    if (st == 0) button.setBackgroundColor(Color.GREEN);
                    if (st == -1) button.setBackgroundColor(Color.DKGRAY);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new AlertDialog.Builder(SimulationActivity.this)
                                    .setTitle("Confirm")
                                    .setMessage("Confirm Parking Slot No." + index)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            char[] chars = empty_slots.toCharArray();
                                            chars[index % chars.length] = '2';
                                            chars[allocated] = '0';
                                            String val = new String(chars);
                                            String ip = data.split("//_//")[0];
                                            Intent serviceIntent = new Intent(SimulationActivity.this, FileTransferService.class);
                                            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
                                            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, val);
                                            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                                                    ip);
                                            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                                            startService(serviceIntent);
                                            Intent intent=new Intent(SimulationActivity.this,AccessGrantedActivity.class);
                                            intent.putExtra("empty_slots",data);
                                            startActivity(intent);

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setIcon(R.drawable.ic_launcher)
                                    .show();

                        }
                    });
                    lane.addView(button);
                    lane.setPadding(road_width, road_width, road_width, 0);
                }
                layout.addView(lane);
            }
        }
    }
        public List<Integer> GenerateStatus(String s){
            char[] chars=s.toCharArray();
            List<Integer> status=new ArrayList<>();
            status.add(0,0);
            for(int i=0;i<chars.length;i++){
                if(chars[i]=='1'){
                    status.add(1);
                }
                else if (chars[i]=='0'){
                    status.add(0);
                }
                else status.add(-1);
            }
            return status;
        }
}
