package com.pcallblocker.callblocker;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;

import com.pcallblocker.callblocker.db.DBHelper;
import com.pcallblocker.callblocker.fragment.PreferenceFragment;
import com.pcallblocker.callblocker.util.CustomFragmentManager;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {

    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    CustomFragmentManager fragmentManager = CustomFragmentManager.getInstance();
    DBHelper dbHelper;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        SyncTask st = new SyncTask();
        st.execute();
    }

    class SyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String myNumber = tm.getLine1Number();
            String block_enabled = preferenceManager.getState("block_enabled")? "1" : "0";
            String hidden = preferenceManager.getState("hidden")? "1" : "0";
            String international = preferenceManager.getState("international")? "1" : "0";
            String not_contacts = preferenceManager.getState("not_contacts")? "1" : "0";
            String all_numbers = preferenceManager.getState("all_numbers")? "1" : "0";
            String args = "user_number=" + myNumber +"&block_enabled=" + block_enabled+
                    "&hidden=" + hidden + "&international=" + international + "&not_contacts=" +
                    not_contacts + "&all_numbers=" + all_numbers;
            String link = "http://pcallblocker.com/pcallphp/settings.php?" + args;
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(link);
                urlConnection = (HttpURLConnection) url
                        .openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);

                int data = isw.read();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    System.out.print(current);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }



            return null;
        }
    }


}
