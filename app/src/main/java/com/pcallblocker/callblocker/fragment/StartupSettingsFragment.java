package com.pcallblocker.callblocker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

/**
 * Crafted by veek on 08.07.16 with love ♥
 */
public class StartupSettingsFragment extends Fragment {

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
        preferenceManager.init(getActivity(), "settings");
        swHidden = (SwitchCompat) rootView.findViewById(R.id.hidden);
        swInt = (SwitchCompat) rootView.findViewById(R.id.international);
        swNotIn = (SwitchCompat) rootView.findViewById(R.id.not_in);
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        preferenceManager.putState("block_enabled", true);
        preferenceManager.putState("hidden", swHidden.isChecked());
        preferenceManager.putState("international", swInt.isChecked());
        preferenceManager.putState("not_contacts", swNotIn.isChecked());
    }
}
