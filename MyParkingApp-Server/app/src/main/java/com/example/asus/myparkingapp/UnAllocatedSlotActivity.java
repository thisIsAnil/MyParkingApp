package com.example.asus.myparkingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by INFIi on 9/22/2016.
 */
public class UnAllocatedSlotActivity extends Activity {
    String empty_slots;
    StringPref stringPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unallocate);

         stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.EMPTY_SLOTS,null);
        empty_slots=stringPref.getStringPref();
        findViewById(R.id.unallocate_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText=(EditText)findViewById(R.id.edit_text);
                String txt=editText.getText().toString();
                int val=Integer.parseInt(txt);
                if(val>empty_slots.length()){
                    Toast.makeText(UnAllocatedSlotActivity.this,"Invalid Slot No",Toast.LENGTH_SHORT).show();
                }
                else {
                    char[] chars=empty_slots.toCharArray();
                    chars[val-1]='0';
                    stringPref=new StringPref(getSharedPreferences(KEYS.PREF_NAME,MODE_PRIVATE),KEYS.EMPTY_SLOTS,null);
                    empty_slots=new String(chars);
                    stringPref.setStringPref(empty_slots);
                    Toast.makeText(UnAllocatedSlotActivity.this,"Update SuccessFul",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UnAllocatedSlotActivity.this,ParkingMainActivity.class));
                }
            }
        });

    }
}
