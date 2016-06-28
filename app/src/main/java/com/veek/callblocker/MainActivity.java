package com.veek.callblocker;


import android.annotation.TargetApi;
import android.app.Service;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.veek.callblocker.DB.BlacklistDAO;
import com.veek.callblocker.Fragment.BlacklistFragment;
import com.veek.callblocker.Fragment.RejectedFragment;
import com.veek.callblocker.Model.Blacklist;
import com.veek.callblocker.Util.BlacklistAdapter;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    ViewPagerAdapter adapter;

    public static AlertDialog alertDialog;

    public static BlacklistDAO blackListDao;
    public static List<Blacklist> blockList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setShowHideAnimationEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0) fab.hide(); else fab.show();
                switch (tab.getPosition()) {
                    case 0:
                        fab.hide();
                        break;
                    case 1:
                        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final View view = (View) getLayoutInflater().inflate(R.layout.dialog_add, null);
                                final EditText etNumber = (EditText) view.findViewById(R.id.etNumber);
                                final EditText etName = (EditText) view.findViewById(R.id.etName);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(R.string.add_entry);
                                builder.setView(view)
                                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final Blacklist phone = new Blacklist();
                                                phone.phoneNumber = etNumber.getText().toString();
                                                phone.phoneName = etName.getText().toString();
                                                TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
                                                if (phone.phoneNumber.equals("")) {
                                                    Toast.makeText(MainActivity.this, R.string.empty_nmb, Toast.LENGTH_LONG).show();
                                                } else  if (MainActivity.blockList.contains(new Blacklist(phone.phoneNumber, phone.phoneName))){
                                                        Toast.makeText(MainActivity.this, R.string.alr_blocked, Toast.LENGTH_LONG).show();
                                                }
                                                    else {
                                                        blackListDao.create(phone);
                                                        blockList.add(new Blacklist(phone.phoneNumber, phone.phoneName));
                                                        BlacklistFragment fragment = (BlacklistFragment) adapter.getItem(viewPager.getCurrentItem());
                                                        if (fragment instanceof BlacklistFragment) {
                                                            fragment.setChanged();
                                                        }
                                                    }
                                                }

                                        });
                                        //.setCancelable(true);
                                alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                        fab.show();
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
    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.phone_missed,
                R.drawable.phone_locked,
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new RejectedFragment(),"ONE");
        adapter.addFrag(new BlacklistFragment(), "TWO");
        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
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

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!(alertDialog == null)) alertDialog.dismiss();
        if (!(BlacklistAdapter.alert == null)) BlacklistAdapter.alert.dismiss();
        if (!(BlacklistAdapter.alertEdit == null)) BlacklistAdapter.alertEdit.dismiss();
        if (!(BlacklistAdapter.alertDelete == null)) BlacklistAdapter.alertDelete.dismiss();

    }
}
