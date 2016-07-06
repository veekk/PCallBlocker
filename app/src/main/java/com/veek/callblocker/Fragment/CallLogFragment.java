package com.veek.callblocker.Fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.veek.callblocker.R;
import com.veek.callblocker.Util.CallLogAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;

/**
 * Crafted by veek on 04.07.16 with love ♥
 */
public class CallLogFragment extends Fragment {


    View rootView;

    public CallLogFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_call_log, null);
        RecyclerView rvCallLog;
        rvCallLog = (RecyclerView) rootView.findViewById(R.id.rvCallLog);
        CallsProvider callsProvider = new CallsProvider(getActivity());
        List<Call> calls = callsProvider.getCalls().getList();
        Collections.reverse(calls);
        CallLogAdapter adapter = new CallLogAdapter(calls, getActivity(), getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvCallLog.setLayoutParams(lp);
        rvCallLog.setLayoutManager(llm);
        rvCallLog.setAdapter(adapter);
        return rootView;
    }
}
