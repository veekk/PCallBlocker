package com.veek.callblocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.veek.callblocker.fragment.PreferenceFragment;
import com.veek.callblocker.util.CustomFragmentManager;
import com.veek.callblocker.util.CustomPreferenceManager;

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
