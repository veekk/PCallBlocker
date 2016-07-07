package com.veek.callblocker.Fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.veek.callblocker.CallLogActivity;
import com.veek.callblocker.ContactListActivity;
import com.veek.callblocker.DB.BlacklistDAO;
import com.veek.callblocker.DB.RejectedCallsDAO;
import com.veek.callblocker.MainActivity;
import com.veek.callblocker.Model.Blacklist;
import com.veek.callblocker.Model.RejectedCall;
import com.veek.callblocker.R;
import com.veek.callblocker.Util.CustomFragmentManager;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

/**
 * Crafted by veek on 05.07.16 with love ♥
 */
public class MainFragment extends Fragment {

    AppCompatActivity activity;
    View rootView;


    CustomFragmentManager fragmentManager = CustomFragmentManager.getInstance();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private FabSpeedDial fab;

    public static AlertDialog alertManual;
    public static AlertDialog alertAdd;

    public static ViewPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, null);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        fab = (FabSpeedDial) rootView.findViewById(R.id.fabSpeedDial);
        fab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_manual_add:
                        final View view = activity.getLayoutInflater().inflate(R.layout.dialog_add, null);
                        final EditText etNumber = (EditText) view.findViewById(R.id.etNumber);
                        final EditText etName = (EditText) view.findViewById(R.id.etName);
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(getResources().getStringArray(R.array.add_from)[0]);
                        builder.setView(view)
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Blacklist phone = new Blacklist();
                                        phone.phoneNumber = etNumber.getText().toString();
                                        phone.phoneName = etName.getText().toString();
                                        if (phone.phoneNumber.equals("")) {
                                            Toast.makeText(activity, R.string.empty_nmb, Toast.LENGTH_SHORT).show();
                                        } else if (MainActivity.blockList.contains(new Blacklist(phone.phoneNumber, phone.phoneName))) {
                                            Toast.makeText(activity, R.string.alr_blocked, Toast.LENGTH_SHORT).show();
                                        } else {
                                            MainActivity.blackListDao.create(phone);
                                            if (phone.phoneName.equals("")) {
                                                ContactsProvider contactsProvider = new ContactsProvider(getActivity());
                                                List<Contact> contacts = contactsProvider.getContacts().getList();
                                                for (Contact contact : contacts){
                                                    if (PhoneNumberUtils.compare(contact.normilizedPhone, phone.phoneNumber)){
                                                        phone.phoneName = contact.displayName;
                                                    }
                                                }
                                            }
                                            MainActivity.blockList.add(new Blacklist(phone.phoneNumber, phone.phoneName));
                                            BlacklistFragment fragment = (BlacklistFragment) adapter.getItem(0);
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
                    case R.id.action_contact_add:
                        startActivity(new Intent(activity, ContactListActivity.class));
                        break;
                }
                return true;
            }
        });


        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
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
                        } catch (Exception e) {

                        }
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

        return rootView;
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
        adapter = new ViewPagerAdapter(activity.getSupportFragmentManager());
        adapter.addFrag(new BlacklistFragment(), "BLACKLIST");
        adapter.addFrag(new CallLogFragment(), "CALL_LOG");
        adapter.addFrag(new RejectedFragment(), "REJECTED");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
        }
    }


}
