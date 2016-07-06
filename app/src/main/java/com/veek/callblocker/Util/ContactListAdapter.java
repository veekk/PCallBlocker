package com.veek.callblocker.Util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.veek.callblocker.DB.BlacklistDAO;
import com.veek.callblocker.MainActivity;
import com.veek.callblocker.Model.Blacklist;
import com.veek.callblocker.R;

import java.text.SimpleDateFormat;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.contacts.Contact;

/**
 * Crafted by veek on 30.06.16 with love ♥
 */
public class ContactListAdapter extends  RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>{

    List<Contact> contacts;
    Context context;
    Activity activity;

    public ContactListAdapter(List<Contact> contacts, Context context, Activity activity) {
        this.contacts = contacts;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_list, parent, false);
        ContactListViewHolder contactListViewHolder = new ContactListViewHolder(v);
        return contactListViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactListViewHolder holder, final int position) {
        holder.tvName.setText(contacts.get(position).displayName);
        holder.tvNumber.setText(contacts.get(position).phone);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.blockList.contains(new Blacklist(contacts.get(position).phone, contacts.get(position).displayName))) {
                    Toast.makeText(activity, R.string.alr_blocked, Toast.LENGTH_LONG).show();
                } else {
                MainActivity.blackListDao = new BlacklistDAO(context);
                MainActivity.blackListDao.create(new Blacklist(contacts.get(position).phone, contacts.get(position).displayName));
                activity.finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactListViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvNumber;
        CardView cardView;
        public ContactListViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvNameCon);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumberCon);
            cardView = (CardView) itemView.findViewById(R.id.cardViewCon);
        }
    }
}