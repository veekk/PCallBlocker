package com.pcallblocker.callblocker.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.Toast;
import com.pcallblocker.callblocker.ContactListActivity;
import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.util.CustomFragmentManager;
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
    public static AlertDialog alertContains;

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(getResources().getStringArray(R.array.add_from)[0]);
                        builder.setView(view)
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Blacklist phone = new Blacklist();
                                        phone.phoneNumber = etNumber.getText().toString();
                                        if (phone.phoneNumber.equals("")) {
                                            Toast.makeText(activity, R.string.empty_nmb, Toast.LENGTH_SHORT).show();
                                        } else if (MainActivity.blockList.contains(new Blacklist(phone.phoneNumber, phone.phoneName))) {
                                            Toast.makeText(activity, R.string.alr_blocked, Toast.LENGTH_SHORT).show();
                                        } else {
                                                ContactsProvider contactsProvider = new ContactsProvider(getActivity());
                                                List<Contact> contacts = contactsProvider.getContacts().getList();
                                                for (Contact contact : contacts){
                                                    if (PhoneNumberUtils.compare(contact.normilizedPhone, phone.phoneNumber)){
                                                        phone.phoneName = contact.displayName;
                                                    }
                                                }

                                            MainActivity.blackListDao.create(phone);
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
                        if (!alertManual.isShowing()) alertManual.show();
                        break;
                    case R.id.action_contact_add:
                        startActivity(new Intent(activity, ContactListActivity.class));
                        break;
                    case R.id.action_last_incoming:
//                        CallsProvider callsProvider = new CallsProvider(getActivity());
//                        int i = 0;
//                        int size = callsProvider.getCalls().getList().size();
//                        List<Call> calls;
//                        if (size > 30) {
//                            calls = callsProvider.getCalls().getList().subList((size - 30), size);
//                        } else calls = callsProvider.getCalls().getList();
//                        Collections.reverse(calls);
//                        for(Call call : calls) {
//                            if ((call.type == Call.CallType.INCOMING || call.type == Call.CallType.MISSED) && call.number != null) {
//                                i++;
//                                if (MainActivity.blockList.contains(new Blacklist(call.number, call.name))) {
//                                    Toast.makeText(activity, R.string.alr_blocked, Toast.LENGTH_SHORT).show();
//                                } else {
//                                    String name = null;
//                                    if (call.name == null) {
//                                        ContactsProvider contactsProvider = new ContactsProvider(getActivity());
//                                        List<Contact> contacts = contactsProvider.getContacts().getList();
//                                        for (Contact contact : contacts) {
//                                            if (PhoneNumberUtils.compare(contact.normilizedPhone, call.number)) {
//                                                name = contact.displayName;
//                                                break;
//                                            }
//                                        }
//                                    } else name = call.name;
//                                    MainActivity.blackListDao.create(new Blacklist(call.number, name));
//                                    MainActivity.blockList = MainActivity.blackListDao.getAllBlacklist();
//                                }
//                                try {
//                                    BlacklistFragment fragment = (BlacklistFragment) adapter.getItem(0);
//                                    if (fragment != null) {
//                                        fragment.reCast();
//                                    }
//                                } catch (Exception e) {
//
//                                }
//                                if (i == 0) {
//                                    Toast.makeText(getActivity(), R.string.last_not_found, Toast.LENGTH_LONG);
//                                }
//                                break;
//                            }
//                        }

                        try {
                            CallLogFragment callFragment = (CallLogFragment) adapter.getItem(1);
                            if (callFragment != null){
                                callFragment.lastIncoming();
                            }
                        } catch (Exception e){}
                                                        try {
                                    BlacklistFragment fragment = (BlacklistFragment) adapter.getItem(0);
                                    if (fragment != null) {
                                        fragment.reCast();
                                    }
                                } catch (Exception e) {

                                }

                        break;
                    case R.id.action_contains:
                        final View v = activity.getLayoutInflater().inflate(R.layout.dialog_contains, null);
                        final EditText etN = (EditText) v.findViewById(R.id.etNumber);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                        mBuilder.setTitle(R.string.add_start_from);
                        mBuilder.setView(v)
                                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Blacklist phone = new Blacklist();
                                        phone.phoneNumber = etN.getText().toString();
                                        if (phone.phoneNumber.equals("")) {
                                            Toast.makeText(activity, R.string.empty_nmb, Toast.LENGTH_SHORT).show();
                                        } else if (MainActivity.blockList.contains(new Blacklist(phone.phoneNumber, phone.phoneName))) {
                                            Toast.makeText(activity, R.string.alr_blocked, Toast.LENGTH_SHORT).show();
                                        } else {
                                            phone.phoneNumber = phone.phoneNumber + "xxxxxxx";
                                            MainActivity.blackListDao.create(phone);
                                            MainActivity.blockList.add(new Blacklist(phone.phoneNumber, phone.phoneName));
                                            BlacklistFragment fragment = (BlacklistFragment) adapter.getItem(0);
                                            if (fragment != null) {
                                                fragment.setChanged();
                                            }
                                        }
                                    }

                                })
                                .setCancelable(true);
                        alertContains = mBuilder.create();
                        if (!alertContains.isShowing()) alertContains.show();
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
                viewPager.setCurrentItem(tab.getPosition());
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
                        try {
                            RejectedFragment fragment = (RejectedFragment) adapter.getItem(2);
                            if (fragment != null) {
                                fragment.reCast();
                            }
                        } catch (Exception e) {

                        }
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
