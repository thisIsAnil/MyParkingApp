package com.example.asus.myparkingapp;

import android.content.SharedPreferences;

/**
 * Created by Asus on 18-09-2016.
 */
public class BoolPref {

    private final SharedPreferences preferences;
    private final String key;
    private final Boolean defaultValue;

    public BoolPref(SharedPreferences preferences, String key) {
        this(preferences, key, false);
    }

    public BoolPref(SharedPreferences preferences, String key, Boolean defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public boolean getBooleanPref() {
        return preferences.getBoolean(key, defaultValue);
    }

    public boolean isSet() {
        return preferences.contains(key);
    }

    public void setBooleanPref(boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public void delete() {
        preferences.edit().remove(key).apply();
    }

}
