package com.pcallblocker.callblocker.util;

/**
 * Crafted by veek on 05.07.16 with love ♥
 */
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Crafted by veek on 22.12.15 with love ♥
 */
public class CustomPreferenceManager {
    static private CustomPreferenceManager instance;

    public static CustomPreferenceManager getInstance() {
        if (instance == null) {
            instance = new CustomPreferenceManager();
        }
        return instance;
    }

    private Context context;
    private String username;
    private SharedPreferences preferences;

    public void init(Context context, String username) {
        this.context = context;
        this.username = username;
        preferences = context.getSharedPreferences(username, Context.MODE_PRIVATE);
    }

    public String getString(String prefKey) {
        SharedPreferences.Editor editor = preferences.edit();
        String result = preferences.getString(prefKey, "");
        return result;
    }

    public boolean getState(String prefkey) {
        SharedPreferences.Editor editor = preferences.edit();
        boolean result = preferences.getBoolean(prefkey, false);
        return result;
    }

    public int getInt(String prefKey) {
        SharedPreferences.Editor editor = preferences.edit();
        int result = preferences.getInt(prefKey, 0);
        return result;
    }

    public void putString(String prefKey, String prefValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefKey, prefValue);
        editor.commit();
    }

    public void putState(String prefKey, boolean prefValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(prefKey, prefValue);
        editor.commit();
    }

    public void putInt(String prefKey, int prefValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(prefKey, prefValue);
        editor.commit();
    }


    public void removeSpecific(String prefKey){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(prefKey);
        editor.commit();
    }

}