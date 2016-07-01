package com.veek.callblocker.Util;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.veek.callblocker.MainActivity;
import com.veek.callblocker.Model.RejectedCall;
import com.veek.callblocker.R;

import java.text.SimpleDateFormat;
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
        if (rejectedCalls.get(position).phoneName.equals("")) holder.tvName.setText(rejectedCalls.get(position).phoneNumber);
        else holder.tvName.setText(rejectedCalls.get(position).phoneName);
        holder.tvAmount.setText(context.getString(R.string.rejected_calls) + " " + Long.toString(rejectedCalls.get(position).amountCalls));
        holder.tvNumber.setText(rejectedCalls.get(position).phoneNumber);
        holder.tvTime.setText(context.getString(R.string.last_call) + " " + sdf.format(rejectedCalls.get(position).time));
                //rejectedCalls.get(position).time);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
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
        CardView cardView;
        public RejectedCallsViewHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumberR);
            tvName = (TextView) itemView.findViewById(R.id.tvNameR);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmountR);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            cardView = (CardView) itemView.findViewById(R.id.card_view_r);
        }
    }

    public RejectedCallsAdapter(Context context, List<RejectedCall> rejectedCalls) {
        this.context = context;
        this.rejectedCalls = rejectedCalls;
    }
}
