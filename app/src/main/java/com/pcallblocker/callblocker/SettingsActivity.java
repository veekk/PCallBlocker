package com.pcallblocker.callblocker;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pcallblocker.callblocker.db.DBHelper;
import com.pcallblocker.callblocker.fragment.PreferenceFragment;
import com.pcallblocker.callblocker.util.CustomFragmentManager;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;
import com.pcallblocker.callblocker.util.CustomRestClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends AppCompatActivity {

    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    CustomFragmentManager fragmentManager = CustomFragmentManager.getInstance();
    DBHelper dbHelper;
    TelephonyManager tm;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        dbHelper = new DBHelper(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        String myNumber = tm.getLine1Number().replace("+", "");
        if (TextUtils.isEmpty(myNumber)) myNumber = "unknown";
        String block_enabled = preferenceManager.getState("block_enabled")? "1" : "0";
        String hidden = preferenceManager.getState("hidden")? "1" : "0";
        String international = preferenceManager.getState("international")? "1" : "0";
        String not_contacts = preferenceManager.getState("not_contacts")? "1" : "0";
        String all_numbers = preferenceManager.getState("all_numbers")? "1" : "0";
        RequestParams params = new RequestParams();
        params.put("user_number", myNumber);
        params.put("block_enabled", block_enabled);
        params.put("hidden", hidden);
        params.put("international", international);
        params.put("not_contacts", not_contacts);
        params.put("all_numbers", all_numbers);
        CustomRestClient customRestClient = new CustomRestClient();
        customRestClient.post("settings.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


}
