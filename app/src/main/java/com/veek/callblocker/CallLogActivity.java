package com.veek.callblocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.veek.callblocker.Util.CallLogAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import me.everything.providers.android.calllog.Call;
import me.everything.providers.android.calllog.CallsProvider;

public class CallLogActivity extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        toolbar = (Toolbar) findViewById(R.id.toolbarCL);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getStringArray(R.array.add_from)[1]);
        RecyclerView rvCallLog;
        rvCallLog = (RecyclerView) findViewById(R.id.rvCallLog);
        CallsProvider callsProvider = new CallsProvider(this);
        List<Call> calls = callsProvider.getCalls().getList();
        Collections.reverse(calls);
        CallLogAdapter adapter = new CallLogAdapter(calls, this, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rvCallLog.setLayoutParams(lp);
        rvCallLog.setLayoutManager(llm);
        rvCallLog.setAdapter(adapter);
    }
}
