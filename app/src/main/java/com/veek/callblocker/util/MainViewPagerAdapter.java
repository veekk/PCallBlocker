package com.veek.callblocker.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import com.veek.callblocker.fragment.BlacklistFragment;
import com.veek.callblocker.fragment.CallLogFragment;
import com.veek.callblocker.fragment.RejectedFragment;

/**
 * Crafted by veek on 05.07.16 with love ♥
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment mCurrentFragment;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new BlacklistFragment();
            case 1:
                return new CallLogFragment();
            case 2:
                return new RejectedFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }


    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }
}
