package com.veek.callblocker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veek.callblocker.DB.BlacklistDAO;
import com.veek.callblocker.Util.BlacklistAdapter;


/**
 * Crafted by veek on 21.06.16 with love ♥
 */

public class BlacklistFragment extends Fragment{

    public RecyclerView rvBlacklist;
    public BlacklistAdapter adapter;
    BlacklistDAO blacklistDAO;
    View rootView;

    public BlacklistFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_blacklist, null);
        rvBlacklist = (RecyclerView) rootView.findViewById(R.id.rvBlacklist);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvBlacklist.setLayoutManager(llm);
        adapter = new BlacklistAdapter(MainActivity.blockList);
        rvBlacklist.setAdapter(adapter);
        return rootView;
    }


    void setChanged(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}
