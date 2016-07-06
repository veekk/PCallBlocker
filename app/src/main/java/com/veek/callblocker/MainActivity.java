package com.veek.callblocker;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.veek.callblocker.DB.BlacklistDAO;
import com.veek.callblocker.DB.RejectedCallsDAO;
import com.veek.callblocker.Fragment.BlacklistFragment;
import com.veek.callblocker.Fragment.CallLogFragment;
import com.veek.callblocker.Fragment.RejectedFragment;
import com.veek.callblocker.Fragment.SettingFragment;
import com.veek.callblocker.Model.Blacklist;
import com.veek.callblocker.Model.RejectedCall;
import com.veek.callblocker.Util.BlacklistAdapter;
import com.veek.callblocker.Util.CustomFragmentManager;

import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    CustomFragmentManager fragmentManager = CustomFragmentManager.getInstance();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private FloatingActionButton fab;

    public static AlertDialog alertManual;
    public static AlertDialog alertAdd;

    public static ViewPagerAdapter adapter;

    public static BlacklistDAO blackListDao;
    public static List<Blacklist> blockList;

    public static RejectedCallsDAO rejectedCallsDAO;
    public static List<RejectedCall> rejectedCalls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                       builder.setItems(getResources().getStringArray(R.array.add_from), new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                               switch (i) {
                                                   case 0:
                                                       final View view = (View) getLayoutInflater().inflate(R.layout.dialog_add, null);
                                                       final EditText etNumber = (EditText) view.findViewById(R.id.etNumber);
                                                       final EditText etName = (EditText) view.findViewById(R.id.etName);
                                                       AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                       builder.setTitle(getResources().getStringArray(R.array.add_from)[0]);
                                                       builder.setView(view)
                                                               .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                                                   @Override
                                                                   public void onClick(DialogInterface dialog, int which) {
                                                                       final Blacklist phone = new Blacklist();
                                                                       phone.phoneNumber = etNumber.getText().toString();
                                                                       phone.phoneName = etName.getText().toString();
                                                                       if (phone.phoneNumber.equals("")) {
                                                                           Toast.makeText(MainActivity.this, R.string.empty_nmb, Toast.LENGTH_SHORT).show();
                                                                       } else if (MainActivity.blockList.contains(new Blacklist(phone.phoneNumber, phone.phoneName))) {
                                                                           Toast.makeText(MainActivity.this, R.string.alr_blocked, Toast.LENGTH_SHORT).show();
                                                                       } else {
                                                                           blackListDao.create(phone);
                                                                           blockList.add(new Blacklist(phone.phoneNumber, phone.phoneName));
                                                                           BlacklistFragment fragment = (BlacklistFragment) adapter.getItem(viewPager.getCurrentItem());
                                                                           if (fragment != null) {
                                                                               fragment.setChanged();
                                                                           }
                                                                       }
                                                                   }

                                                               })
                                                               .setCancelable(true);
                                                       alertManual = builder.create();
                                                       alertManual.show();
                                                       break;
                                                   case 1:
                                                       startActivity(new Intent(MainActivity.this, CallLogActivity.class));
                                                       break;
                                                   case 2:
                                                       startActivity(new Intent(MainActivity.this, ContactListActivity.class));
                                                       break;
                                               }
                                           }
                                       });
                                       alertAdd = builder.create();
                                       alertAdd.show();
                                   }
                               });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        try {
                            BlacklistFragment fragment = (BlacklistFragment) adapter.getItem(0);
                            if (fragment != null) {
                                fragment.reCast();
                            }
                        }
                        catch (Exception e){

                        }
                        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        fab.show();
                        break;
                    case 1:
                        fab.hide();
                        break;
                    case 2:
                        fab.hide();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();
        fragmentManager.init(this, R.id.viewpager);
        fragmentManager.setFragment(new SettingFragment(), true);

    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.phone_locked,
                R.drawable.phone_log,
                R.drawable.phone_missed
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new BlacklistFragment(), "BLACKLIST");
        adapter.addFrag(new CallLogFragment(), "CALL_LOG");
        adapter.addFrag(new RejectedFragment(), "REJECTED");
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
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
