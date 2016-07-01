package com.veek.callblocker.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.veek.callblocker.MainActivity;
import com.veek.callblocker.Model.RejectedCall;
import com.veek.callblocker.R;
import com.veek.callblocker.Util.BlacklistAdapter;
import com.veek.callblocker.Util.RejectedCallsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.everything.providers.android.contacts.Contact;

/**
 * Crafted by veek on 21.06.16 with love ♥
 */
public class RejectedFragment extends Fragment {

    public RecyclerView rvRejectedCalls;
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
        rvRejectedCalls = (RecyclerView) rootView.findViewById(R.id.rvRejectedCalls);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvRejectedCalls.setLayoutParams(lp);
        rvRejectedCalls.setLayoutManager(llm);
        adapter = new RejectedCallsAdapter(getActivity(), MainActivity.rejectedCalls);
        rvRejectedCalls.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        rvRejectedCalls = (RecyclerView) rootView.findViewById(R.id.rvRejectedCalls);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvRejectedCalls.setLayoutParams(lp);
        rvRejectedCalls.setLayoutManager(llm);
        adapter = new RejectedCallsAdapter(getActivity(), MainActivity.rejectedCalls);
        rvRejectedCalls.setAdapter(adapter);
    }


}
