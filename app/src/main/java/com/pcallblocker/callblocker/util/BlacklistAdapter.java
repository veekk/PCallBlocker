package com.pcallblocker.callblocker.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.model.Blacklist;
import com.pcallblocker.callblocker.R;

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
        holder.lLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (!alertDelete.isShowing()) alertDelete.show();


            }

        });
    }

    public static class BlacklistViewHolder extends RecyclerView.ViewHolder{
        TextView tvNumber;
        TextView tvName;
        LinearLayout lLay;
        public BlacklistViewHolder(View itemView) {
            super(itemView);
            lLay = (LinearLayout) itemView.findViewById(R.id.lLay);
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
