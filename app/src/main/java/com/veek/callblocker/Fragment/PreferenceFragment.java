package com.veek.callblocker.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dpizarro.pinview.library.PinView;
import com.veek.callblocker.R;
import com.veek.callblocker.Util.CustomPreferenceManager;

/**
 * Crafted by veek on 05.07.16 with love ♥
 */
public class PreferenceFragment extends android.preference.PreferenceFragment{

    CustomPreferenceManager preferenceManager = CustomPreferenceManager.getInstance();
    SwitchPreference swEnaled;
    SwitchPreference swHidden;
    SwitchPreference swInternational;
    SwitchPreference swNotContacts;
    SwitchPreference swAllNumbers;


    InputMethodManager imm;


    Preference pin;

    SwitchPreference swPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefrences);

        preferenceManager.init(getActivity(), "settings");

        swEnaled = (SwitchPreference) findPreference("block_enabled");
        swHidden = (SwitchPreference) findPreference("hidden");
        swInternational = (SwitchPreference) findPreference("international");
        swNotContacts = (SwitchPreference) findPreference("not_contacts");
        swAllNumbers = (SwitchPreference) findPreference("all_numbers");
        swPassword = (SwitchPreference) findPreference("password_on");

        pin = (Preference) findPreference("pin");

        swEnaled.setChecked(preferenceManager.getState("block_enabled"));
        swHidden.setChecked(preferenceManager.getState("hidden"));
        swInternational.setChecked(preferenceManager.getState("international"));
        swNotContacts.setChecked(preferenceManager.getState("not_contacts"));
        swAllNumbers.setChecked(preferenceManager.getState("all_numbers"));
        swPassword.setChecked(preferenceManager.getState("password_on"));

        pin.setEnabled(swPassword.isChecked());

        swEnaled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("block_enabled", !swEnaled.isChecked());
                return true;
            }
        });
        swHidden.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("hidden", !swHidden.isChecked());
                return true;
            }
        });
        swInternational.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("international", !swInternational.isChecked());
                return true;
            }
        });
        swNotContacts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("not_contacts", !swNotContacts.isChecked());
                return true;
            }
        });
        swAllNumbers.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preferenceManager.putState("all_numbers", !swAllNumbers.isChecked());
                return true;
            }
        });

        swPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (swPassword.isChecked()) pinDisablingDialog();
                else {
                    if (preferenceManager.getString("pin").equals("")) pinChangeDialog();
                    else {
                        preferenceManager.putState("password_on", !swPassword.isChecked());
                        pin.setEnabled(!swPassword.isChecked());
                    }
                }
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
        builder.setTitle("Enter current PIN")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        if (pinView.getPinResults().equals(preferenceManager.getString("pin"))){
                            dialogInterface.dismiss();
                            pinChangeDialog();
                        } else {
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Toast.makeText(getActivity(), "Entered PIN incorrect", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pinDisablingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = View.inflate(getActivity(), R.layout.dialog_pin, null);
        builder.setTitle("Enter current PIN")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        swPassword.setChecked(true);

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        if (pinView.getPinResults().equals(preferenceManager.getString("pin"))){
                            dialogInterface.dismiss();
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            preferenceManager.putState("password_on", false);
                        } else {
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Toast.makeText(getActivity(), "Entered PIN incorrect", Toast.LENGTH_LONG).show();
                            swPassword.setChecked(true);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pinChangeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = View.inflate(getActivity(), R.layout.dialog_pin, null);
        builder.setTitle("Enter new PIN")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PinView pinView = (PinView) view.findViewById(R.id.pinView);
                        if (preferenceManager.getString("pin").equals("")) {
                            pin.setEnabled(true);
                            preferenceManager.putState("password_on", !swPassword.isChecked());}
                            preferenceManager.putString("pin", pinView.getPinResults());
                            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            Toast.makeText(getActivity(), "PIN changed succesfully", Toast.LENGTH_LONG).show();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
