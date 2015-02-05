package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.yscn.knucommunity.CustomView.BaseDoubleKillActivity;
import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 10. 22..
 */
public class MainActivity extends BaseDoubleKillActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        viewInit();
        findViewById(R.id.main_studentground).setOnClickListener(this);
        findViewById(R.id.main_notice).setOnClickListener(this);
        findViewById(R.id.main_market).setOnClickListener(this);
        findViewById(R.id.main_community).setOnClickListener(this);
        findViewById(R.id.main_link).setOnClickListener(this);
        findViewById(R.id.main_setting).setOnClickListener(this);
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.board_white_pirmary_dark_color));
        }
        getSupportActionBar().hide();
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