package com.veek.callblocker;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import com.stephentuso.welcome.WelcomeScreenBuilder;
import com.stephentuso.welcome.ui.NextButton;
import com.stephentuso.welcome.ui.WelcomeFragmentHolder;
import com.stephentuso.welcome.util.WelcomeScreenConfiguration;
import com.veek.callblocker.Fragment.PinFragment;
import com.veek.callblocker.Fragment.StartupSettingsFragment;
import com.veek.callblocker.Fragment.TermsFragment;

/**
 * Crafted by veek on 08.07.16 with love ♥
 */

public class MyWelcomeActivity extends com.stephentuso.welcome.ui.WelcomeActivity {

    @Override
    protected WelcomeScreenConfiguration configuration() {
        return new WelcomeScreenBuilder(this)
                .theme(R.style.WelcomeScreenTheme)
                .defaultBackgroundColor(R.color.primary)
                .titlePage(R.drawable.pencil, "Title", R.color.primary)
                .page(new WelcomeFragmentHolder() {
                    @Override
                    protected Fragment fragment() {
                        return new TermsFragment();
                    }
                }, R.color.primary)
                .basicPage(R.drawable.phone_incoming, "Header", "More text.", R.color.primary)
                .basicPage(R.drawable.phone_missed, "Header 2", "More text 2", R.color.primary)
                .page(new WelcomeFragmentHolder() {
                    @Override
                    protected Fragment fragment() {
                        return new StartupSettingsFragment();
                    }
                }, R.color.primary)
                .page(new WelcomeFragmentHolder() {
                    @Override
                    protected Fragment fragment() {
                        return new PinFragment();
                    }
                }, R.color.primary)
                .basicPage(R.drawable.check, "You're welcome", "Let's start!", R.color.primary)
                .canSkip(false)
                .build();

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }



}
