package com.infi.myparkingapp_client;

import android.content.SharedPreferences;

/**
 * Created by INFIi on 9/22/2016.
 */
public class IntPref {

    private final SharedPreferences preferences;
    private final String key;
    private final int defaultValue;

    public IntPref(SharedPreferences preferences, String key) {
        this(preferences, key, 0);
    }

    public IntPref(SharedPreferences preferences, String key, int defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public int get() {
        return preferences.getInt(key, defaultValue);
    }

    public boolean isSet() {
        return preferences.contains(key);
    }

    public void set(int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public void delete() {
        preferences.edit().remove(key).apply();
    }
}
