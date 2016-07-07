package com.veek.callblocker.Fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.veek.callblocker.MainActivity;
import com.veek.callblocker.R;
import com.veek.callblocker.Util.BlacklistAdapter;
import com.veek.callblocker.Util.DividerItemDecoration;


/**
 * Crafted by veek on 21.06.16 with love ♥
 */

public class BlacklistFragment extends Fragment {

    public RecyclerView rvBlacklist;
    public BlacklistAdapter adapter;
    View rootView;

    public static BlacklistFragment newInstance() {
        return new BlacklistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_blacklist, null);
        reCast();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setChanged(){
        if (adapter != null) adapter.notifyItemInserted(adapter.getItemCount()); else reCast();
    }

    @Override
    public void onResume() {
        super.onResume();
        reCast();
    }

    public void reCast (){
        if (rootView != null) {
            rvBlacklist = (RecyclerView) rootView.findViewById(R.id.rvBlacklist);
            Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
            RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rvBlacklist.setLayoutParams(lp);
            rvBlacklist.setLayoutManager(llm);
            rvBlacklist.addItemDecoration(dividerItemDecoration);
            adapter = new BlacklistAdapter(MainActivity.blockList, getActivity());
            rvBlacklist.setAdapter(adapter);
        }
    }



    @Override
    public void onPause() {
        super.onPause();
    }
}
