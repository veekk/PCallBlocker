package com.veek.callblocker.Util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * Created by veek on 30.11.15.
 */

public class CustomFragmentManager {

    private static CustomFragmentManager instance;

    private Activity activity;
    private FragmentManager fragmentManager;
    private int containerResId;

    private CustomFragmentManager(){
    }



    public static CustomFragmentManager getInstance() {

        if (instance == null) {
            instance = new CustomFragmentManager();
        }

        return instance;
    }


    public void init(Activity activity, int containerResId){
        this.activity = activity;
        fragmentManager = activity.getFragmentManager();
        this.containerResId = containerResId;
    }


    public void setFragment(Fragment fragment, boolean addToBackStack) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerResId, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }


    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }


}