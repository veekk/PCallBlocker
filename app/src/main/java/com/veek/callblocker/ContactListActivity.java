package com.veek.callblocker;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.veek.callblocker.Util.ContactListAdapter;
import com.veek.callblocker.Util.DividerItemDecoration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);

        ContactListAdapter adapter = new ContactListAdapter(contacts, this, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvCallLog.addItemDecoration(dividerItemDecoration);
        rvCallLog.setLayoutParams(lp);
        rvCallLog.setLayoutManager(llm);
        rvCallLog.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }


}
