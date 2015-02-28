package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yscn.knucommunity.CustomView.BaseNavigationDrawerActivity;
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
public class SettingActivity extends BaseNavigationDrawerActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        attatchView(R.layout.activity_setting);
        toolbarInit();
        getFragmentManager().beginTransaction()
                .replace(R.id.setting_framelayout, new PreferenceItem()).commit();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void toolbarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.text_setting_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.setting_main_color));
    }

    public static class PreferenceItem extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference mMyInfoNamePreference, mMyInfoAccountPreference,
                mMyInfoAuthorDevices, mMyInfoPhoneNumber, mDeleteAccountPreference,
                mDeveloperInfo, mFeedBack, mHelpPreference, mInviteFriendPreference;
        private CheckBoxPreference mNotificationPreference;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_preferences_item);

            mMyInfoNamePreference = findPreference("setting_preference_myinfo_name");
            mMyInfoAccountPreference = findPreference("setting_preference_myinfo_myaccountinfo");
            mMyInfoPhoneNumber = findPreference("setting_preference_myinfo_phonenumber");
            mMyInfoAuthorDevices = findPreference("setting_preference_myinfo_authordevices");
            mDeleteAccountPreference = findPreference("setting_preference_myinfo_deleteaccount");
            mDeveloperInfo = findPreference("setting_preference_developerinfo");
            mFeedBack = findPreference("setting_preference_feedback");
            mNotificationPreference = (CheckBoxPreference) findPreference("setting_preference_notification");
            mHelpPreference = findPreference("setting_preference_helper");
            mInviteFriendPreference = findPreference("setting_preference_invite_friend");

            mMyInfoNamePreference.setTitle(UserData.getInstance().getStudentName());
            mMyInfoNamePreference.setSummary(UserData.getInstance().getStudentNumber());
            initNeedNetworkData();

            mMyInfoNamePreference.setOnPreferenceClickListener(this);
            mMyInfoAccountPreference.setOnPreferenceClickListener(this);
            mMyInfoPhoneNumber.setOnPreferenceClickListener(this);
            mMyInfoAuthorDevices.setOnPreferenceClickListener(this);
            mDeleteAccountPreference.setOnPreferenceClickListener(this);
            mDeveloperInfo.setOnPreferenceClickListener(this);
            mFeedBack.setOnPreferenceClickListener(this);
            mNotificationPreference.setOnPreferenceChangeListener(this);
            mHelpPreference.setOnPreferenceClickListener(this);
            mInviteFriendPreference.setOnPreferenceClickListener(this);
        }

        private void initNeedNetworkData() {
            new AsyncTask<Void, Void, Boolean>() {
                String phoneNumber;

                @Override
                protected void onPreExecute() {
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
                        AlertToast.success(getActivity(), getString(R.string.success_phone_input));
                        return;
                    }
                    String reason = jsonObject.get("reason").toString();
                    AlertToast.error(getActivity(), reason);
                }
            }.execute();
        }

        private void deleteUser(String password) {
            new AsyncTask<String, Void, JSONObject>() {
                private ClearProgressDialog clearProgressDialog;

                @Override
                protected void onPreExecute() {
                    clearProgressDialog = new ClearProgressDialog(getActivity());
                    clearProgressDialog.show();
                }

                @Override
                protected JSONObject doInBackground(String... params) {
                    try {
                        return NetworkUtil.getInstance().checkIsLoginUser().deleteAccount(params[0]);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    clearProgressDialog.cancel();
                    if (jsonObject != null) {
                        String result = jsonObject.get("result").toString();

                        if (result.equals("success")) {
                            AlertToast.success(getActivity(), R.string.success_delete_account);
                            UserData.getInstance().logoutUser();
                            return;
                        }

                        if (result.equals("fail")) {
                            String reason = jsonObject.get("reason").toString();
                            if (reason.equals("loginfail")) {
                                AlertToast.error(getActivity(), R.string.error_password);
                            }
                        }
                    } else {
                        AlertToast.error(getActivity(), R.string.error_to_work);
                    }
                }
            }.execute(password);
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

        private void showPhoneNumberChangeDialog() {
            LinearLayout linearLayout = new LinearLayout(getActivity());
            final EditText editText = new EditText(getActivity());
            editText.setHint(R.string.phone_input_hint);
            editText.setBackgroundColor(Color.TRANSPARENT);
            linearLayout.addView(editText);
            int padding = (int) ApplicationUtil.getInstance().dpToPx(20);
            linearLayout.setPadding(padding, 0, padding, 0);
            ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
            editText.setLayoutParams(layoutParams);
            if (numberCheck(mMyInfoPhoneNumber.getSummary().toString())) {
                editText.setText(mMyInfoPhoneNumber.getSummary());
            }

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String phonenumber = editText.getText().toString();
                            if (!phonenumber.isEmpty()) {
                                updatePhoneNumber(phonenumber);
                            } else {
                                AlertToast.warning(getActivity(), R.string.warning_phone_input);
                            }
                        }
                    })
                    .setTitle(R.string.notify_title)
                    .setMessage(R.string.text_setting_phonenumber_dialog_message)
                    .setNegativeButton(R.string.NO, null)
                    .setView(linearLayout)
                    .show();
            ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();

            if (key.equals(mMyInfoNamePreference.getKey())) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
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
                ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
                return true;
            } else if (key.equals(mMyInfoAccountPreference.getKey())) {
                startActivity(new Intent(getActivity(), StudentInfoActivity.class));
                getActivity().finish();
                return true;
            } else if (key.equals(mMyInfoAuthorDevices.getKey())) {
                startActivity(new Intent(getActivity(), AuthorDeviceActivity.class));
                return true;
            } else if (key.equals(mMyInfoPhoneNumber.getKey())) {
                showPhoneNumberChangeDialog();
            } else if (key.equals(mDeleteAccountPreference.getKey())) {
                showAccountDeleteDialog();
                return true;
            } else if (key.equals(mDeveloperInfo.getKey())) {
                startActivity(new Intent(getActivity(), DeveloperInfoActivity.class));
                return true;
            } else if (key.equals(mFeedBack.getKey())) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:kh4975@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_feedback_subject));
                startActivity(Intent.createChooser(intent, null));
                return true;
            } else if (key.equals(mHelpPreference.getKey())) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
                return true;
            } else if (key.equals(mInviteFriendPreference.getKey())) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.notify_title)
                        .setMessage(R.string.text_invite_friend_message)
                        .setPositiveButton(R.string.OK, null)
                        .show();
                ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
                return true;
            }
            return false;
        }

        private void showAccountDeleteDialog() {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.warning_title)
                    .setMessage(R.string.warning_account_delete_message)
                    .setNegativeButton(R.string.NO, null)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LinearLayout linearLayout = new LinearLayout(getActivity());
                            final EditText editText = new EditText(getActivity());
                            editText.setBackgroundColor(Color.TRANSPARENT);
                            linearLayout.addView(editText);
                            int padding = (int) ApplicationUtil.getInstance().dpToPx(20);
                            linearLayout.setPadding(padding, 0, padding, 0);
                            ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            editText.setLayoutParams(layoutParams);

                            AlertDialog innerAlertDialog = new AlertDialog.Builder(getActivity())
                                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String password = editText.getText().toString();
                                            deleteUser(password);
                                        }
                                    })
                                    .setTitle(R.string.notify_title)
                                    .setCancelable(false)
                                    .setMessage(R.string.input_password_hint)
                                    .setNegativeButton(R.string.NO, null)
                                    .setView(linearLayout)
                                    .show();
                            ApplicationUtil.getInstance().setTypeFace(innerAlertDialog.getWindow().getDecorView());
                        }
                    })
                    .show();
            ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (key.equals(mMyInfoPhoneNumber.getKey())) {
                String phoneNumber = newValue.toString();
                updatePhoneNumber(phoneNumber);
                return true;
            } else if (key.equals(mNotificationPreference.getKey())) {
                return true;
            }
            return false;
        }
    }
}
