package com.pcallblocker.callblocker;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.dpizarro.pinview.library.PinView;
import com.stephentuso.welcome.WelcomeScreenHelper;
import com.pcallblocker.callblocker.db.BlacklistDAO;
import com.pcallblocker.callblocker.db.RejectedCallsDAO;
import com.pcallblocker.callblocker.fragment.MainFragment;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.model.RejectedCall;
import com.pcallblocker.callblocker.service.NotifyService;
import com.pcallblocker.callblocker.util.BlacklistAdapter;
import com.pcallblocker.callblocker.util.CustomFragmentManager;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

import io.fabric.sdk.android.Fabric;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    CustomFragmentManager fragmentManager = CustomFragmentManager.getInstance();
    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();

    public static AlertDialog alertManual;
    public static AlertDialog alertAdd;

    public static BlacklistDAO blackListDao;
    public static List<Blacklist> blockList;

    public static RejectedCallsDAO rejectedCallsDAO;
    public static List<RejectedCall> rejectedCalls;

    WelcomeScreenHelper welcomeScreen;



    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        fragmentManager.init(this, R.id.cLay);
        preferenceManager.init(this, "settings");

        if (preferenceManager.getState("notification_on")){
            startService(new Intent(this, NotifyService.class));
        }

        if (!preferenceManager.getState("notFirst")) {
            preferenceManager.putState("block_enabled", true);
            welcomeScreen = new WelcomeScreenHelper(this, MyWelcomeActivity.class);
            welcomeScreen.forceShow();
        } else {
            if (!preferenceManager.getState("password_on")) {
                fragmentManager.setFragment(new MainFragment(), false);
            } else {
                pinCheckingDialog();
            }
        }


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
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_settings1:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","support@pcallblocker.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Private Call Blocker feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_email)));
                break;
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getResources().getString(R.string.share_body);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Private Call Blocker");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));
                break;
            case R.id.action_rate:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //Try Google play
                intent.setData(Uri.parse("market://details?id=[Id]"));
                if (!MyStartActivity(intent)) {
                    //Market (Google play) app seems not installed, let's try to open a webbrowser
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?[Id]"));
                    if (!MyStartActivity(intent)) {
                        //Well if this also fails, we have run out of options, inform the user.
                        Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean MyStartActivity(Intent aIntent) {
        try
        {
            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == WelcomeScreenHelper.DEFAULT_WELCOME_SCREEN_REQUEST) {
            // The key of the welcome screen is in the Intent

            if (resultCode == RESULT_OK) {
                preferenceManager.putState("notFirst", true);
                fragmentManager.setFragment(new MainFragment(), false);
            } else {
                finish();
            }

        }

    }

    private void pinCheckingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = View.inflate(this, R.layout.dialog_pin, null);
        builder.setTitle(R.string.pin_enter)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        finish();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        if (pinView.getPinResults().equals(preferenceManager.getString("pin"))) {
                            fragmentManager.setFragment(new MainFragment(), false);
                            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        } else {
                            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            finish();
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
