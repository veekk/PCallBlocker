package com.pcallblocker.callblocker.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcallblocker.callblocker.MainActivity;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.util.DividerItemDecoration;
import com.pcallblocker.callblocker.util.RejectedCallsAdapter;

/**
 * Crafted by veek on 21.06.16 with love ♥
 */
public class RejectedFragment extends Fragment {

    public RecyclerView rvRejectedCalls;
    TextView tvEmpty;
    public RejectedCallsAdapter adapter;
    View rootView;

    public RejectedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_rejected, null);
            reCast();
        return rootView;
    }

    @Override
    public void onResume() {
        if (rootView != null) reCast();
        super.onResume();

    }

    public void reCast(){
        rvRejectedCalls = (RecyclerView) rootView.findViewById(R.id.rvRejectedCalls);
        tvEmpty = (TextView) rootView.findViewById(R.id.empty_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvRejectedCalls.setLayoutParams(lp);
        rvRejectedCalls.setLayoutManager(llm);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        rvRejectedCalls.addItemDecoration(dividerItemDecoration);
        adapter = new RejectedCallsAdapter(getActivity(), MainActivity.rejectedCalls);
        if (MainActivity.rejectedCalls.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else tvEmpty.setVisibility(View.GONE);
        rvRejectedCalls.setAdapter(adapter);
    }


}
