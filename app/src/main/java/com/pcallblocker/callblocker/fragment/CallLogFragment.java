package com.pcallblocker.callblocker.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.model.RejectedCall;
import com.pcallblocker.callblocker.util.CallLogAdapter;
import com.pcallblocker.callblocker.util.DividerItemDecoration;

import org.json.JSONArray;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

/**
 * Crafted by veek on 04.07.16 with love ♥
 */
public class CallLogFragment extends Fragment {


    View rootView;
    RecyclerView rvCallLog;
    CallLogAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    public static List<Call> calls;

    public CallLogFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_call_log, null);
        if (calls == null)
        reCast(true); else  reCast(false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                CallsProvider callsProvider = new CallsProvider(getActivity());
                calls = callsProvider.getCalls().getList();
                Collections.sort(calls, new Comparator<Call>() {
                    @Override
                    public int compare(Call call1, Call call2) {
                        return Long.valueOf(call1.callDate).compareTo(call2.callDate);
                    }
                });
                Collections.reverse(calls);
                adapter.addAll(calls);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        return rootView;
    }

    public void lastIncoming(){
        int i = 0;
        for(Call call : calls) {
            if ((call.type == Call.CallType.INCOMING || call.type == Call.CallType.MISSED) && call.number != null) {
                if (call.number.length() >= 3) {
                    i++;
                    if (MainActivity.blockList.contains(new Blacklist(call.number, call.name))) {
                        Toast.makeText(getActivity(), R.string.alr_blocked, Toast.LENGTH_SHORT).show();
                    } else {
                        String name = null;
                        if (call.name == null) {
                            ContactsProvider contactsProvider = new ContactsProvider(getActivity());
                            List<Contact> contacts = contactsProvider.getContacts().getList();
                            for (Contact contact : contacts) {
                                if (PhoneNumberUtils.compare(contact.normilizedPhone, call.number)) {
                                    name = contact.displayName;
                                    break;
                                }
                            }
                        } else name = call.name;
                        MainActivity.blackListDao.create(new Blacklist(call.number, name));
                        MainActivity.blockList = MainActivity.blackListDao.getAllBlacklist();
                    }

                    if (i == 0) {
                        Toast.makeText(getActivity(), R.string.last_not_found, Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        }
    }

    public void reCast(boolean refresh){
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);

        if (refresh){
        CallsProvider callsProvider = new CallsProvider(getActivity());
        calls = callsProvider.getCalls().getList();
            Collections.sort(calls, new Comparator<Call>() {
                @Override
                public int compare(Call call1, Call call2) {
                    return Long.valueOf(call1.callDate).compareTo(call2.callDate);
                }
            });
            Collections.reverse(calls);
        }
        adapter= new CallLogAdapter(calls, getActivity(), getActivity());

        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(5);
        set.addAnimation(animation);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        animation.setDuration(60);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 1.0f);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvCallLog = (RecyclerView) rootView.findViewById(R.id.rvContactList);
        rvCallLog.setLayoutAnimation(controller);
        rvCallLog.addItemDecoration(dividerItemDecoration);
        rvCallLog.setLayoutParams(lp);
        rvCallLog.setLayoutManager(llm);
        rvCallLog.swapAdapter(adapter, false);
    }


}
