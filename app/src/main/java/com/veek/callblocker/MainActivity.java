package com.veek.callblocker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.veek.callblocker.DB.BlacklistDAO;
import com.veek.callblocker.DB.RejectedCallsDAO;
import com.veek.callblocker.Fragment.MainFragment;
import com.veek.callblocker.Model.Blacklist;
import com.veek.callblocker.Model.RejectedCall;
import com.veek.callblocker.Util.BlacklistAdapter;
import com.veek.callblocker.Util.CustomFragmentManager;

import io.fabric.sdk.android.Fabric;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    CustomFragmentManager fragmentManager = CustomFragmentManager.getInstance();

    public static AlertDialog alertManual;
    public static AlertDialog alertAdd;

    public static BlacklistDAO blackListDao;
    public static List<Blacklist> blockList;

    public static RejectedCallsDAO rejectedCallsDAO;
    public static List<RejectedCall> rejectedCalls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        fragmentManager.init(this, R.id.cLay);
        fragmentManager.setFragment(new MainFragment(), false);

    }


    @Override
    protected void onResume() {
        super.onResume();

        blackListDao = new BlacklistDAO(this);
        blockList = blackListDao.getAllBlacklist();

        rejectedCallsDAO = new RejectedCallsDAO(this);
        rejectedCalls = rejectedCallsDAO.getAllRejectedCalls();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_settings1:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (alertManual != null) if (alertManual.isShowing()) alertManual.dismiss();
        if (BlacklistAdapter.alert != null)
            if (BlacklistAdapter.alert.isShowing()) BlacklistAdapter.alert.dismiss();
        if (BlacklistAdapter.alertEdit != null)
            if (BlacklistAdapter.alertEdit.isShowing()) BlacklistAdapter.alertEdit.dismiss();
        if (BlacklistAdapter.alertDelete != null)
            if (BlacklistAdapter.alertDelete.isShowing()) BlacklistAdapter.alertDelete.dismiss();
        if (alertAdd != null) if (alertAdd.isShowing()) alertAdd.dismiss();
    }


}
