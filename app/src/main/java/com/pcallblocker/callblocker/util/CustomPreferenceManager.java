package com.pcallblocker.callblocker.util;

/**
 * Crafted by veek on 05.07.16 with love ♥
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

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

    public void init (Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getString(String prefKey) {
        String result = preferences.getString(prefKey, "");
        return result;
    }

    public Set<String> getStringSet(String key){
        return preferences.getStringSet(key, new HashSet<String>());
    }

    public boolean getState(String prefkey) {
        boolean result = preferences.getBoolean(prefkey, false);
        return result;
    }

    public void putString(String prefKey, String prefValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(prefKey, prefValue);
        editor.apply();
    }

    public void putState(String prefKey, boolean prefValue) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(prefKey, prefValue);
        editor.apply();
    }


}