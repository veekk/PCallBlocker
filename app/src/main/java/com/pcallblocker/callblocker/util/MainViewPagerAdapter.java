package com.pcallblocker.callblocker.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import com.pcallblocker.callblocker.fragment.BlacklistFragment;
import com.pcallblocker.callblocker.fragment.CallLogFragment;
import com.pcallblocker.callblocker.fragment.RejectedFragment;

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
