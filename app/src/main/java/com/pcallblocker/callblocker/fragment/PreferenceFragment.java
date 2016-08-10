package com.pcallblocker.callblocker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dpizarro.pinview.library.PinView;
import com.pcallblocker.callblocker.R;
import com.pcallblocker.callblocker.service.NotifyService;
import com.pcallblocker.callblocker.util.CustomPreferenceManager;

/**
 * Crafted by veek on 05.07.16 with love ♥
 */
public class PreferenceFragment extends android.preference.PreferenceFragment{

    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    SwitchPreference swEnabled;
    SwitchPreference swHidden;
    SwitchPreference swInternational;
    SwitchPreference swNotContacts;
    SwitchPreference swAllNumbers;
    SwitchPreference swNotification;


    InputMethodManager imm;


    Preference pin;

    SwitchPreference swPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);

        preferenceManager.init(getActivity(), "settings");

        swEnabled = (SwitchPreference) findPreference("block_enabled");
        swHidden = (SwitchPreference) findPreference("hidden");
        swInternational = (SwitchPreference) findPreference("international");
        swNotContacts = (SwitchPreference) findPreference("not_contacts");
        swAllNumbers = (SwitchPreference) findPreference("all_numbers");
        swPassword = (SwitchPreference) findPreference("password_on");
        swNotification = (SwitchPreference) findPreference("notification_on");

        pin =  findPreference("pin");
//
//        swEnaled.setChecked(preferenceManager.getState("block_enabled"));
//        swHidden.setChecked(preferenceManager.getState("hidden"));
//        swInternational.setChecked(preferenceManager.getState("international"));
//        swNotContacts.setChecked(preferenceManager.getState("not_contacts"));
//        swAllNumbers.setChecked(preferenceManager.getState("all_numbers"));
//        swPassword.setChecked(preferenceManager.getState("password_on"));
//        swNotification.setChecked(preferenceManager.getState("notification_on"));

        pin.setEnabled(swPassword.isChecked());

//        swEnaled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                preferenceManager.putState("block_enabled", !swEnaled.isChecked());
//                return true;
//            }
//        });
//        swHidden.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                preferenceManager.putState("hidden", !swHidden.isChecked());
//                return true;
//            }
//        });
//        swInternational.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                preferenceManager.putState("international", !swInternational.isChecked());
//                return true;
//            }
//        });
//        swNotContacts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                preferenceManager.putState("not_contacts", !swNotContacts.isChecked());
//                return true;
//            }
//        });
//        swAllNumbers.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o) {
//                preferenceManager.putState("all_numbers", !swAllNumbers.isChecked());
//                return true;
//            }
//        });

        swPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (swPassword.isChecked()) pinDisablingDialog();
                else {
                    if (preferenceManager.getString("pin").equals("")) pinSetupDialog();
                    else {
                        preferenceManager.putState("password_on", !swPassword.isChecked());
                        pin.setEnabled(!swPassword.isChecked());
                    }
                }
                return true;
            }
        });

        swNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("notification_on", !swNotification.isChecked());
                if (preferenceManager.getState("notification_on")){
                    getActivity().startService(new Intent(getActivity(), NotifyService.class));
                } else getActivity().stopService(new Intent(getActivity(), NotifyService.class));
                return true;
            }
        });

        pin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                pinCheckingDialog();
                return true;
            }
        });

    }


    private void pinCheckingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = View.inflate(getActivity(), R.layout.dialog_pin, null);
        builder.setTitle(R.string.current_pin_dialog)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        if (pinView.getPinResults().equals(preferenceManager.getString("pin"))){
                            dialogInterface.dismiss();
                            pinChangeDialog();
                        } else {
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Toast.makeText(getActivity(), R.string.invalid_pin, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pinDisablingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = View.inflate(getActivity(), R.layout.dialog_pin, null);
        builder.setTitle(R.string.current_pin_dialog)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        swPassword.setChecked(true);

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        if (pinView.getPinResults().equals(preferenceManager.getString("pin"))){
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            preferenceManager.putState("password_on", false);
                            pin.setEnabled(false);
                        } else {
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Toast.makeText(getActivity(), R.string.invalid_pin, Toast.LENGTH_LONG).show();
                            swPassword.setChecked(true);
                        }
                    }
                })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pinChangeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = View.inflate(getActivity(), R.layout.dialog_pin, null);
        builder.setTitle(R.string.pin_new)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        if (preferenceManager.getString("pin").equals("")) {
                            pin.setEnabled(true);
                            preferenceManager.putState("password_on", !swPassword.isChecked());}
                            preferenceManager.putString("pin", pinView.getPinResults());
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Toast.makeText(getActivity(), R.string.pin_change_succ, Toast.LENGTH_LONG).show();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pinSetupDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = View.inflate(getActivity(), R.layout.dialog_pin, null);
        builder.setTitle(R.string.pin_new)
                .setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        pin.setEnabled(true);
                        preferenceManager.putState("password_on", true);
                        preferenceManager.putString("pin", pinView.getPinResults());
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        Toast.makeText(getActivity(), R.string.pin_setup_succ, Toast.LENGTH_LONG).show();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPause() {
        preferenceManager.putState("block_enabled", swEnabled.isChecked());
        preferenceManager.putState("hidden", swHidden.isChecked());
        preferenceManager.putState("international", swInternational.isChecked());
        preferenceManager.putState("not_contacts", swNotContacts.isChecked());
        preferenceManager.putState("all_numbers", swAllNumbers.isChecked());
        preferenceManager.putState("password_on", swPassword.isChecked());
        preferenceManager.putState("notification_on", swNotification.isChecked());
        super.onPause();
    }
}
