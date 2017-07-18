package com.pcallblocker.callblocker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

/**
 * Crafted by veek on 08.07.16 with love ♥
 */
public class StartupSettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    View rootView;
    SwitchCompat swHidden;
    SwitchCompat swInt;
    SwitchCompat swNotIn;
    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, null);
        preferenceManager.init(getActivity().getApplicationContext(), "settings");

        preferenceManager.putState("block_enabled", true);
        preferenceManager.putState("hidden", true);
        preferenceManager.putState("international", true);
        preferenceManager.putState("not_contacts", false);
        swHidden = (SwitchCompat) rootView.findViewById(R.id.hidden);
        swInt = (SwitchCompat) rootView.findViewById(R.id.international);
        swNotIn = (SwitchCompat) rootView.findViewById(R.id.not_in);
        swHidden.setOnCheckedChangeListener(this);
        swInt.setOnCheckedChangeListener(this);
        swNotIn.setOnCheckedChangeListener(this);
        return rootView;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.hidden:
                preferenceManager.putState("hidden", isChecked);
                break;
            case R.id.international:
                preferenceManager.putState("international", isChecked);
                break;
            case R.id.not_in:
                preferenceManager.putState("not_contacts", isChecked);
                break;
        }
    }
}
