package com.veek.callblocker.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veek.callblocker.R;
import com.veek.callblocker.Util.CustomPreferenceManager;

/**
 * Crafted by veek on 05.07.16 with love ♥
 */
public class PreferenceFragment extends android.preference.PreferenceFragment{

    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    SwitchPreference swEnaled;
    SwitchPreference swHidden;
    SwitchPreference swInternational;
    SwitchPreference swNotContacts;
    SwitchPreference swAllNumbers;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);

        preferenceManager.init(getActivity(), "settings");

        swEnaled = (SwitchPreference) findPreference("block_enabled");
        swHidden = (SwitchPreference) findPreference("hidden");
        swInternational = (SwitchPreference) findPreference("international");
        swNotContacts = (SwitchPreference) findPreference("not_contacts");
        swAllNumbers = (SwitchPreference) findPreference("all_numbers");

        swEnaled.setChecked(preferenceManager.getState("block_enabled"));
        swHidden.setChecked(preferenceManager.getState("hidden"));
        swInternational.setChecked(preferenceManager.getState("international"));
        swNotContacts.setChecked(preferenceManager.getState("not_contacts"));
        swAllNumbers.setChecked(preferenceManager.getState("all_numbers"));

        swEnaled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("block_enabled", !swEnaled.isChecked());
                return true;
            }
        });
        swHidden.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("hidden", !swHidden.isChecked());
                return true;
            }
        });
        swInternational.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("international", !swInternational.isChecked());
                return true;
            }
        });
        swNotContacts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("not_contacts", !swNotContacts.isChecked());
                return true;
            }
        });
        swAllNumbers.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("all_numbers", !swAllNumbers.isChecked());
                return true;
            }
        });
    }

    


}
