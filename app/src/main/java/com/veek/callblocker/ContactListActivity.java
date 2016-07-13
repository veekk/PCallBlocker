package com.veek.callblocker;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.veek.callblocker.Util.ContactListAdapter;
import com.veek.callblocker.Util.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

public class ContactListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RecyclerView rvCallLog;
    List<Contact> contacts;
    ContactListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        toolbar = (Toolbar) findViewById(R.id.toolbarCL);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.add_from)[2]);

        rvCallLog = (RecyclerView) findViewById(R.id.rvCallLog);
        ContactsProvider contactsProvider = new ContactsProvider(this);
        contacts = contactsProvider.getContacts().getList();
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact, Contact t1) {
                return contact.displayName.compareToIgnoreCase(t1.displayName);
            }
        });

        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        rvCallLog.addItemDecoration(dividerItemDecoration);


        ContactListAdapter adapter = new ContactListAdapter(contacts, this, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        rvCallLog.setLayoutParams(lp);
        rvCallLog.setLayoutManager(llm);
        rvCallLog.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setOnQueryTextListener(listener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

            final List<Contact> filteredList = new ArrayList<>();

            for (int i = 0; i < contacts.size(); i++) {

                final String text = contacts.get(i).displayName.toLowerCase();
                if (text.contains(query)) {

                    filteredList.add(contacts.get(i));
                }
            }

            rvCallLog.setLayoutManager(new LinearLayoutManager(ContactListActivity.this));
            adapter = new ContactListAdapter(filteredList, ContactListActivity.this, ContactListActivity.this);
            rvCallLog.setAdapter(adapter);
            adapter.notifyDataSetChanged();  // data set changed
            return true;

        }
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };


}
