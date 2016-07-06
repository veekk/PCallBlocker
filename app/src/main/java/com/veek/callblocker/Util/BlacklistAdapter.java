package com.veek.callblocker.Util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.veek.callblocker.MainActivity;
import com.veek.callblocker.Model.Blacklist;
import com.veek.callblocker.R;

import java.util.List;

/**
 * Crafted by veek on 21.06.16 with love ♥
 */
public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.BlacklistViewHolder> {



    public List<Blacklist> blacklist;
    static public AlertDialog alert;
    static public AlertDialog alertEdit;
    static public AlertDialog alertDelete;
    Context context;

    @Override
    public BlacklistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blacklist, parent, false);
        BlacklistViewHolder blacklistViewHolder = new BlacklistViewHolder(v);
        return blacklistViewHolder;
    }

    @Override
    public void onBindViewHolder(BlacklistViewHolder holder, final int position) {
        holder.tvNumber.setText(blacklist.get(position).phoneNumber);
        if (blacklist.get(position).phoneName==null) holder.tvName.setText(blacklist.get(position).phoneNumber);
        else if (blacklist.get(position).phoneName.equals("")) holder.tvName.setText(blacklist.get(position).phoneNumber);
        else holder.tvName.setText(blacklist.get(position).phoneName);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(context.getResources().getStringArray(R.array.items_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null);
                final EditText etNumber = (EditText) view.findViewById(R.id.etNumber);
                final EditText etName = (EditText) view.findViewById(R.id.etName);
                etNumber.setText(MainActivity.blockList.get(position).phoneNumber);
                etName.setText(MainActivity.blockList.get(position).phoneName);
                etNumber.setEnabled(false);
                AlertDialog.Builder builderEdit = new AlertDialog.Builder(context);
                builderEdit.setTitle(R.string.edit_title)
                        .setView(view)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.blockList.get(position).phoneName = etName.getText().toString();
                                MainActivity.blackListDao.update(MainActivity.blockList.get(position));
                                notifyItemChanged(position);
                            }

                        });
                //.setCancelable(true);
                alertEdit = builderEdit.create();
                alertEdit.show();
                                break;
                            case 1:
                                AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);
                                builderDelete.setTitle(R.string.delete_q)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                MainActivity.blackListDao.delete(MainActivity.blockList.get(position));
                                                MainActivity.blockList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setCancelable(true);
                                alertDelete = builderDelete.create();
                                alertDelete.show();
                                break;
                        }
                    }
                });
                alert = builder.create();
                alert.show();


            }

        });
    }

    public static class BlacklistViewHolder extends RecyclerView.ViewHolder{
        TextView tvNumber;
        TextView tvName;
        CardView cardView;
        public BlacklistViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumber);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
        }
    }

    public BlacklistAdapter(List<Blacklist> blacklist, Context context){
        this.blacklist = blacklist;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return blacklist.size();
    }
}
