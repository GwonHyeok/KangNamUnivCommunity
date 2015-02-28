package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UserData;
import com.yscn.knucommunity.Util.UserDataPreference;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;


public class Splash extends ActionBarActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String GCM_SENDER_ID = "513704487484";
    private GoogleCloudMessaging googleCloudMessaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /* 스팰래쉬 화면이므로 액션바 제거 */
        getSupportActionBar().hide();

        View view = findViewById(R.id.splash);
        int height = ApplicationUtil.getInstance().getScreenHeight();
        int width = ApplicationUtil.getInstance().getScreenWidth();

        Bitmap splash = ApplicationUtil.getInstance().decodeSampledBitmap(
                getResources(),
                R.drawable.bg_splash,
                width,
                height);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), splash);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(bitmapDrawable);
        } else {
            view.setBackgroundDrawable(bitmapDrawable);
        }

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        final int SPLASH_DELAY_TIME = 1300;

        new AsyncTask<Void, Void, JSONObject>() {
            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    cancel(true);
                    appCloseDialog(getString(R.string.error_check_network_state));
                    return;
                }
                progressBar = (ProgressBar) findViewById(R.id.splash_progressbar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().serverStatusCheck();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                if (jsonObject == null) {
                    appCloseDialog(getString(R.string.error_to_work));
                    return;
                }

                String result = jsonObject.get("result").toString();

                if (result.equals("success")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            if (loginCheck()) {
                    /* 로그인이 되어있음 메인 액티비티 호출 */
                                startActivity(new Intent(getContext(), MainActivity.class));
                    /* GCM ID 등록 */
                                if (checkPlayServices()) {
                                    checkGCMRegisterID();
                                }
                            } else {
                    /* 로그인이 되어있지 않음 로그인 액티비티 호출 */
                                startActivity(new Intent(getContext(), LoginActivity.class));
                            }
                        }

                    }, SPLASH_DELAY_TIME);
                } else if (result.equals("fail")) {
                    appCloseDialog(jsonObject.get("message").toString());
                }
            }
        }.execute();
    }

    private void appCloseDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.warning_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
        ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
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

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private boolean loginCheck() {
        UserDataPreference userDataPreference = new UserDataPreference(getContext());
        if (userDataPreference.getStudentNumber() == null) {
            return false;
        } else {
            UserData.getInstance().setStudentName(userDataPreference.getStudentName());
            UserData.getInstance().setStudentNumber(userDataPreference.getStudentNumber());
            UserData.getInstance().setUserToken(userDataPreference.getToken());
            UserData.getInstance().setUserRating(userDataPreference.getStudentRating());
            return true;
        }
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
                finish();
            }
            return false;
        }
        return true;
    }

    private Context getContext() {
        return Splash.this;
    }
}
