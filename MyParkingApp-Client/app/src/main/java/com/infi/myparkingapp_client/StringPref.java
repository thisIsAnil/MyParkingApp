package com.infi.myparkingapp_client;

import android.content.SharedPreferences;

/**
 * Created by INFIi on 8/29/2016.
 */
public class StringPref {
    private final SharedPreferences preferences;
    private final String key;
    private final String defaultValue;


    public StringPref(SharedPreferences preferences, String key, String defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getStringPref() {
        return preferences.getString(key, defaultValue);
    }

    public boolean isSet() {
        return preferences.contains(key);
    }

    public void setStringPref(String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void delete() {
        preferences.edit().remove(key).apply();
    }
}

