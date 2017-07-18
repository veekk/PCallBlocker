package com.pcallblocker.callblocker.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pcallblocker.callblocker.MyWelcomeActivity;
import com.pcallblocker.callblocker.R;

/**
 * Crafted by veek on 08.07.16 with love ♥
 */
public class TermsFragment extends Fragment {

    View rootView;
    Button privacy;
    Button terms;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_terms, null);


        terms = (Button) rootView.findViewById(R.id.btnTerms);
        privacy = (Button) rootView.findViewById(R.id.btnPrivacy);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pcallblocker.com/terms-of-service.html"));
                startActivity(browserIntent);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pcallblocker.com/date-policy.html"));
                startActivity(browserIntent);
            }
        });


        return rootView;
    }


}
