package com.pcallblocker.callblocker.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.util.CallLogAdapter;
import com.pcallblocker.callblocker.util.DividerItemDecoration;

import java.util.Collections;
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

        reCast();

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

    public void reCast(){
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);

        CallsProvider callsProvider = new CallsProvider(getActivity());
        calls = callsProvider.getCalls().getList();
        Collections.reverse(calls);
        CallLogAdapter adapter = new CallLogAdapter(calls, getActivity(), getActivity());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        rvCallLog = (RecyclerView) rootView.findViewById(R.id.rvContactList);
        rvCallLog.addItemDecoration(dividerItemDecoration);
        rvCallLog.setLayoutParams(lp);
        rvCallLog.setLayoutManager(llm);
        rvCallLog.swapAdapter(adapter, false);
    }
}
