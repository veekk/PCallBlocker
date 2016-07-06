package com.veek.callblocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.veek.callblocker.Fragment.PreferenceFragment;
import com.veek.callblocker.Util.CustomFragmentManager;
import com.veek.callblocker.Util.CustomPreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    CustomFragmentManager fragmentManager = CustomFragmentManager.getInstance();
    CheckBox cbBlockEnable;
    CheckBox cbHidden;
    CheckBox cbInternational;
    CheckBox cbNotContacts;
    CheckBox cbAllNumbers;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);

        fragmentManager.init(this, R.id.Flay);
        fragmentManager.setFragment(new PreferenceFragment(), false);


//
//        cbBlockEnable = (CheckBox) findViewById(R.id.cbBlockEnable);
//        cbHidden = (CheckBox) findViewById(R.id.cbHidden);
//        cbInternational = (CheckBox) findViewById(R.id.cbInternational);
//        cbNotContacts = (CheckBox) findViewById(R.id.cbNotContacts);
//        cbAllNumbers = (CheckBox) findViewById(R.id.cbAllNumbers);
//
//        preferenceManager.init(this, "settings");
//
//        cbBlockEnable.setChecked(preferenceManager.getState("block_enabled"));
//        cbHidden.setChecked(preferenceManager.getState("hidden"));
//        cbInternational.setChecked(preferenceManager.getState("international"));
//        cbNotContacts.setChecked(preferenceManager.getState("not_contacts"));
//        cbAllNumbers.setChecked(preferenceManager.getState("all_numbers"));
//
//        cbBlockEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                preferenceManager.putState("block_enabled", b);
//            }
//        });
//        cbHidden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                preferenceManager.putState("hidden", b);
//            }
//        });
//        cbInternational.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                preferenceManager.putState("international", b);
//            }
//        });
//        cbNotContacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                preferenceManager.putState("not_contacts", b);
//            }
//        });
//        cbAllNumbers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                preferenceManager.putState("all_numbers", b);
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
