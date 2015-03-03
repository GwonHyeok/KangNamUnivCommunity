package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.yscn.knucommunity.CustomView.BaseDoubleKillActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 10. 22..
 */
public class MainActivity extends BaseDoubleKillActivity implements View.OnClickListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String GCM_SENDER_ID = "513704487484";
    private GoogleCloudMessaging googleCloudMessaging;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        viewInit();
        isShowingHelp();
        findViewById(R.id.main_studentground).setOnClickListener(this);
        findViewById(R.id.main_notice).setOnClickListener(this);
        findViewById(R.id.main_market).setOnClickListener(this);
        findViewById(R.id.main_community).setOnClickListener(this);
        findViewById(R.id.main_link).setOnClickListener(this);
        findViewById(R.id.main_setting).setOnClickListener(this);
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void checkRegisterGcmID() {
        /* GCM ID 등록 */
        if (checkPlayServices()) {
            checkGCMRegisterID();
        }
    }

    private void checkGCMRegisterID() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                googleCloudMessaging = GoogleCloudMessaging.getInstance(getContext());
            }

            @Override
            protected Void doInBackground(Void... params) {
                String registerID, appVersion;

                // GCMID 있는지 확인
                // GCMID 가 있으면 appVersion 을 확인 appVersion 이 현재와 다르면 GCMID 다시 받고 서버에 등록
                // GCMID 가 없으면 아이디 받고 서버에 등록
                try {
                    /* 만약 로그인이 안되어 있다면 return */
                    if (!NetworkUtil.getInstance().isLoginUser()) {
                        return null;
                    }

                    String[] data = NetworkUtil.getInstance().getGCMRegisterData();
                    registerID = data[0];
                    appVersion = data[1];

                    if (registerID.isEmpty() || Integer.parseInt(appVersion) != getAppVersion(getContext())) {
                        if (googleCloudMessaging == null) {
                            googleCloudMessaging = GoogleCloudMessaging.getInstance(getContext());
                        }
                        registerID = googleCloudMessaging.register(GCM_SENDER_ID);
                        int currentAppVersion = getAppVersion(getContext());
                        NetworkUtil.getInstance().checkIsLoginUser().registerGCMID(currentAppVersion, registerID);
                    }

//                    Log.d(getClass().getSimpleName(), "Register ID : " + registerID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void value) {

            }
        }.execute();
    }

    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(getClass().getSimpleName(), "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkRegisterGcmID();
    }

    private Context getContext() {
        return MainActivity.this;
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.board_white_pirmary_dark_color));
        }
        getSupportActionBar().hide();
    }

    private void isShowingHelp() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNeedShowHelp = sharedPreferences.getBoolean("isneedshowhelp", true);
        if (isNeedShowHelp) {
            startActivity(new Intent(this, HelpActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.main_studentground) {
            startActivity(new Intent(this, StudentGroundActivity.class));
        } else if (id == R.id.main_notice) {
            startActivity(new Intent(this, NoticeActivity.class));
        } else if (id == R.id.main_market) {
            startActivity(new Intent(this, MarketMainActivity.class));
        } else if (id == R.id.main_community) {
            startActivity(new Intent(this, CommunittyActivity.class));
        } else if (id == R.id.main_link) {
            startActivity(new Intent(this, LinkActivity.class));
        } else if (id == R.id.main_setting) {
            startActivity(new Intent(this, SettingActivity.class));
        }
    }
}