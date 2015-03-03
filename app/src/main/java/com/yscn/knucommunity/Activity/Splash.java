package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;


public class Splash extends ActionBarActivity {

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
            boolean isLoginUser = false;

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
                    isLoginUser = NetworkUtil.getInstance().isLoginUser();
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
                            if (isLoginUser) {
                                /* 로그인이 되어있음 메인 액티비티 호출 */
                                startActivity(new Intent(getContext(), MainActivity.class));
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

    private Context getContext() {
        return Splash.this;
    }
}
