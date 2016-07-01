package com.veek.callblocker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.veek.callblocker.Util.CallLogAdapter;
import com.veek.callblocker.Util.ContactListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

public class ContactListActivity extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        toolbar = (Toolbar) findViewById(R.id.toolbarCL);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.add_from)[2]);
        RecyclerView rvCallLog;
        rvCallLog = (RecyclerView) findViewById(R.id.rvCallLog);
        ContactsProvider contactsProvider = new ContactsProvider(this);
        List<Contact> contacts = contactsProvider.getContacts().getList();
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact t1) {
                return contact.displayName.compareToIgnoreCase(t1.displayName);
            }
        });

        ContactListAdapter adapter = new ContactListAdapter(contacts, this, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvCallLog.setLayoutParams(lp);
        rvCallLog.setLayoutManager(llm);
        rvCallLog.setAdapter(adapter);
    }
}
