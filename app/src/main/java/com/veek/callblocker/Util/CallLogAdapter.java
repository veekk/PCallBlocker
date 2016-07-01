package com.veek.callblocker.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

/**
 * Crafted by veek on 30.06.16 with love ♥
 */
public class CallLogAdapter extends  RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>{

    List<Call> calls;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    Context context;
    Activity activity;

    public CallLogAdapter(List<Call> calls, Context context, Activity activity) {
        this.calls = calls;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_log, parent, false);
        CallLogViewHolder callLogViewHolder = new CallLogViewHolder(v);
        return callLogViewHolder;
    }

    @Override
    public void onBindViewHolder(CallLogViewHolder holder, final int position) {
        holder.tvName.setText(calls.get(position).name);
        holder.tvNumber.setText(calls.get(position).number);
        holder.tvTime.setText(sdf.format(calls.get(position).callDate));
        switch (calls.get(position).type){
            case INCOMING:
                holder.ivStatus.setImageResource(R.drawable.call_recieved);
                holder.tvCallType.setText(R.string.incoming_call);
                break;
            case OUTGOING:
                holder.ivStatus.setImageResource(R.drawable.call_made);
                holder.tvCallType.setText(R.string.outgoing_call);
                break;
            case MISSED:
                holder.ivStatus.setImageResource(R.drawable.call_missed);
                holder.tvCallType.setText(R.string.missed_call);
                break;
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.blockList.contains(new Blacklist(calls.get(position).number, calls.get(position).name))) {
                    Toast.makeText(activity, R.string.alr_blocked, Toast.LENGTH_LONG).show();
                } else {
                MainActivity.blackListDao = new BlacklistDAO(context);
                MainActivity.blackListDao.create(new Blacklist(calls.get(position).number, calls.get(position).name));
                activity.finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return calls.size();
    }

    public static class CallLogViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvNumber;
        TextView tvTime;
        TextView tvCallType;
        ImageView ivStatus;
        CardView cardView;
        public CallLogViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvNameCL);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumberCL);
            tvTime = (TextView) itemView.findViewById(R.id.tvTimeCL);
            tvCallType = (TextView) itemView.findViewById(R.id.tvCallType);
            ivStatus = (ImageView) itemView.findViewById(R.id.ivCallLogStatus);
            cardView = (CardView) itemView.findViewById(R.id.cardViewCL);
        }
    }
}
