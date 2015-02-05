package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 15. 2. 5..
 */
public class SettingActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting);
        toolbarInit();
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_framelayout, new PreferenceItem()).commit();
    }

    private void toolbarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle(getString(R.string.text_setting_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(0xFF5321A8);
    }

    public static class PreferenceItem extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference mMyInfoNamePreference, mMyInfoAccountPreference, mMyInfoAuthorDevices;
        private EditTextPreference mMyInfoPhoneNumber;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_preferences_item);

            mMyInfoNamePreference = findPreference("setting_preference_myinfo_name");
            mMyInfoAccountPreference = findPreference("setting_preference_myinfo_myaccountinfo");
            mMyInfoPhoneNumber = (EditTextPreference) findPreference("setting_preference_myinfo_phonenumber");
            mMyInfoAuthorDevices = findPreference("setting_preference_myinfo_authordevices");

            mMyInfoNamePreference.setTitle(UserData.getInstance().getStudentName());
            mMyInfoNamePreference.setSummary(UserData.getInstance().getStudentNumber());
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit();
            initNeedNetworkData();

            EditText phoneNumberEditText = mMyInfoPhoneNumber.getEditText();
            phoneNumberEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            phoneNumberEditText.setHint(R.string.warning_phone_input);

            mMyInfoNamePreference.setOnPreferenceClickListener(this);
            mMyInfoAccountPreference.setOnPreferenceClickListener(this);
            mMyInfoPhoneNumber.setOnPreferenceChangeListener(this);
            mMyInfoAuthorDevices.setOnPreferenceClickListener(this);
        }

        private void initNeedNetworkData() {
            new AsyncTask<Void, Void, Boolean>() {
                String phoneNumber;

                @Override
                protected void onPreExecute() {
                    mMyInfoPhoneNumber.setText("");
                    mMyInfoPhoneNumber.setSummary(getString(R.string.text_setting_getting_data));
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    NetworkUtil networkUtil = NetworkUtil.getInstance().checkIsLoginUser();
                    try {
                        JSONObject jsonObject = networkUtil.getPhoneNumber();
                        phoneNumber = jsonObject.get("data").toString();
                        return true;
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean value) {
                    if (value) {
                        mMyInfoPhoneNumber.setSummary(phoneNumber);
                        mMyInfoPhoneNumber.setText(phoneNumber);
                    } else {
                        mMyInfoPhoneNumber.setSummary(getString(R.string.community_board_nodata));
                        AlertToast.error(getActivity(), R.string.error_to_work);
                    }
                }
            }.execute();
        }

        private boolean numberCheck(String number) {
            return number.matches("^[0-9]+$");
        }

        private void updatePhoneNumber(final String phonenumber) {
            new AsyncTask<Void, Void, JSONObject>() {
                private ClearProgressDialog clearProgressDialog;

                @Override
                protected void onPreExecute() {
                    if (phonenumber.isEmpty() || !numberCheck(phonenumber)) {
                        AlertToast.warning(getActivity(), getString(R.string.warning_phone_input));
                        cancel(true);
                        return;
                    }
                    clearProgressDialog = new ClearProgressDialog(getActivity());
                    clearProgressDialog.show();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    try {
                        return NetworkUtil.getInstance().checkIsLoginUser().registerPhoneNumber(phonenumber);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    clearProgressDialog.cancel();
                    if (jsonObject == null) {
                        AlertToast.error(getActivity(), getString(R.string.error_to_work));
                        return;
                    }

                    String result = jsonObject.get("result").toString();
                    if (result.equals("success")) {
                        mMyInfoPhoneNumber.setSummary(phonenumber);
                        mMyInfoPhoneNumber.setText(phonenumber);
                        AlertToast.success(getActivity(), getString(R.string.success_phone_input));
                        return;
                    }
                    String reason = jsonObject.get("reason").toString();
                    AlertToast.error(getActivity(), reason);
                }
            }.execute();
        }

        private void logoutUser() {
            new AsyncTask<Void, Void, Boolean>() {
                private ClearProgressDialog clearProgressDialog;

                @Override
                protected void onPreExecute() {
                    if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                        clearProgressDialog = new ClearProgressDialog(getActivity());
                        clearProgressDialog.show();
                    } else {
                        AlertToast.error(getActivity(), R.string.error_check_network_state);
                        cancel(true);
                    }
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        NetworkUtil.getInstance().checkIsLoginUser().doLogout();
                        return true;
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean bool) {
                    clearProgressDialog.cancel();
                    if (bool) {
                        UserData.getInstance().logoutUser();
                    }
                }
            }.execute();
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();

            if (key.equals(mMyInfoNamePreference.getKey())) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.warning_title)
                        .setMessage(R.string.text_are_you_want_logout_message)
                        .setNegativeButton(R.string.NO, null)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logoutUser();
                            }
                        })
                        .show();
                return true;
            } else if (key.equals(mMyInfoAccountPreference.getKey())) {
                startActivity(new Intent(getActivity(), StudentInfoActivity.class));
                getActivity().finish();
                return true;
            } else if (key.equals(mMyInfoAuthorDevices.getKey())) {
                startActivity(new Intent(getActivity(), AuthorDeviceActivity.class));
                return true;
            }
            return false;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (key.equals(mMyInfoPhoneNumber.getKey())) {
                String phoneNumber = newValue.toString();
                updatePhoneNumber(phoneNumber);
                return true;
            }
            return false;
        }
    }
}
