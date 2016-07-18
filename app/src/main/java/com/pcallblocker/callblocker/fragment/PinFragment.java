package com.pcallblocker.callblocker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import com.dpizarro.pinview.library.PinView;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

/**
 * Crafted by veek on 08.07.16 with love ♥
 */
public class PinFragment extends Fragment {

    View rootView;
    SwitchCompat swPin;
    PinView pinView;
    InputMethodManager imm;
    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pin, null);
        preferenceManager.init(getActivity(), "settings");
        swPin = (SwitchCompat) rootView.findViewById(R.id.swPin);
        pinView = (PinView) rootView.findViewById(R.id.pinView);
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        swPin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    pinView.setVisibility(View.VISIBLE);
                } else {
                    pinView.setVisibility(View.GONE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (swPin.isChecked()) {
            preferenceManager.putState("password_on", swPin.isChecked());
            preferenceManager.putString("pin", pinView.getPinResults());
        }
    }
}
