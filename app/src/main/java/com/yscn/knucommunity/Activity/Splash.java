package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import com.yscn.knucommunity.R;


public class Splash extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /* 스팰래쉬 화면이므로 액션바 제거 */
        getSupportActionBar().hide();

        int SPLASH_DELAY_TIME = 1300;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                if (loginCheck()) {
                    /* 로그인이 되어있음 메인 액티비티 호출 */
                    startActivity(new Intent(getContext(), MainActivity.class));
                } else {
                    /* 로그인이 되어있지 않음 로그인 액티비티 호출 */
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            }
        }, SPLASH_DELAY_TIME);
    }

    private boolean loginCheck() {
        return false;
    }

    private Context getContext() {
        return Splash.this;
    }
}
