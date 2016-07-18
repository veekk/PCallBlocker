package com.pcallblocker.callblocker.util;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.model.RejectedCall;
import com.pcallblocker.callblocker.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Crafted by veek on 29.06.16 with love ♥
 */
public class RejectedCallsAdapter extends RecyclerView.Adapter<RejectedCallsAdapter.RejectedCallsViewHolder> {

    Context context;
    public List<RejectedCall> rejectedCalls;
    AlertDialog alert;
    AlertDialog alertDelete;
    Date date = new Date();

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    @Override
    public RejectedCallsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rejected_call, parent, false);
        RejectedCallsViewHolder rejectedCallsViewHolder = new RejectedCallsViewHolder(v);
        return rejectedCallsViewHolder;
    }

    @Override
    public void onBindViewHolder(RejectedCallsViewHolder holder, final int position) {
        if (rejectedCalls.get(position).phoneName == null) holder.tvName.setText(rejectedCalls.get(position).phoneNumber);
        else if (rejectedCalls.get(position).phoneName.equals("")) holder.tvName.setText(rejectedCalls.get(position).phoneNumber);
        else holder.tvName.setText(rejectedCalls.get(position).phoneName);
        holder.tvAmount.setText(" " + Long.toString(rejectedCalls.get(position).amountCalls));
        holder.tvNumber.setText(rejectedCalls.get(position).phoneNumber);
        holder.tvTime.setText(sdf.format(rejectedCalls.get(position).time));
                //rejectedCalls.get(position).time);
        switch (rejectedCalls.get(position).type) {
            case "not_contacts":
                holder.tvType.setText(R.string.reject_not);
                break;
            case "all_numbers":
                holder.tvType.setText(R.string.reject_all);
                break;
            case "international":
                holder.tvType.setText(R.string.international_reject);
                break;
            case "blacklist":
                holder.tvType.setText(R.string.reject_black);
        }
        holder.lLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(context.getResources().getStringArray(R.array.rejected_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + MainActivity.rejectedCalls.get(position).phoneNumber));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                context.startActivity(callIntent);
                                break;
                            case 1:
                                AlertDialog.Builder builderDelete = new AlertDialog.Builder(context);
                                builderDelete.setTitle(R.string.delete_q)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                MainActivity.rejectedCallsDAO.delete(MainActivity.rejectedCalls.get(position));
                                                MainActivity.rejectedCalls.remove(position);
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

    @Override
    public int getItemCount() {
        return rejectedCalls.size();
    }

    public static class RejectedCallsViewHolder extends RecyclerView.ViewHolder{
        TextView tvNumber;
        TextView tvName;
        TextView tvAmount;
        TextView tvTime;
        TextView tvType;
        RelativeLayout lLay;
        public RejectedCallsViewHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumberR);
            tvName = (TextView) itemView.findViewById(R.id.tvNameR);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmountR);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvType = (TextView) itemView.findViewById(R.id.blockType);
            lLay = (RelativeLayout) itemView.findViewById(R.id.lLay);
        }
    }

    public RejectedCallsAdapter(Context context, List<RejectedCall> rejectedCalls) {
        this.context = context;
        Collections.sort(rejectedCalls, new Comparator<RejectedCall>() {
            @Override
            public int compare(RejectedCall rejectedCall, RejectedCall t1) {
                return rejectedCall.time.compareTo(t1.time);
            }
        });
        Collections.reverse(rejectedCalls);

        this.rejectedCalls = rejectedCalls;
    }
}
