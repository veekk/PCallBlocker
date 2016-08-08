package com.pcallblocker.callblocker;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import com.stephentuso.welcome.WelcomeScreenBuilder;
import com.stephentuso.welcome.ui.WelcomeFragmentHolder;
import com.stephentuso.welcome.util.WelcomeScreenConfiguration;
import com.pcallblocker.callblocker.fragment.StartupSettingsFragment;
import com.pcallblocker.callblocker.fragment.TermsFragment;

/**
 * Crafted by veek on 08.07.16 with love ♥
 */

public class MyWelcomeActivity extends com.stephentuso.welcome.ui.WelcomeActivity {

    @Override
    protected WelcomeScreenConfiguration configuration() {
        return new WelcomeScreenBuilder(this)
                .theme(R.style.WelcomeScreenTheme)
                .defaultBackgroundColor(R.color.primary)
                .page(new WelcomeFragmentHolder() {
                    @Override
                    protected Fragment fragment() {
                        return new TermsFragment();
                    }
                }, R.color.primary)
                .page(new WelcomeFragmentHolder() {
                    @Override
                    protected Fragment fragment() {
                        return new StartupSettingsFragment();
                    }
                }, R.color.primary)
                .swipeToDismiss(true)
                .canSkip(false)
                .build();

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }



}
