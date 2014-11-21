package com.yscn.knucommunity.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class GreenLightDetailActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_greenlightdetail);
        viewInit();
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFFFAFAFA);
        }

         /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_community_base, null);
        ((TextView) view.findViewById(R.id.actionbar_community_base_title)).setText("");
        ((ImageView) view.findViewById(R.id.actionbar_community_base_image)).setImageResource(R.drawable.ic_cancel);
        ((ImageView) view.findViewById(R.id.actionbar_community_first_image)).setImageResource(R.drawable.ic_trash);
        ((ImageView) view.findViewById(R.id.actionbar_community_second_image)).setImageResource(R.drawable.ic_share);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAFAFA")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);

        /* 그린라이트 버튼 */
        findViewById(R.id.greenlight_light_on).setOnClickListener(this);
        findViewById(R.id.greenlight_light_off).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.greenlight_light_on) {
            clickGreenRightButton(true);
        } else if (id == R.id.greenlight_light_off) {
            clickGreenRightButton(false);
        }
    }

    private void clickGreenRightButton(boolean isOn) {
        TextView lightOn = (TextView) findViewById(R.id.greenlight_light_on);
        TextView lightOff = (TextView) findViewById(R.id.greenlight_light_off);
        lightOn.setBackgroundResource(R.drawable.ic_light_on_pressed);
        lightOff.setBackgroundResource(R.drawable.ic_light_off_pressed);

        lightOn.setText("7");
        lightOff.setText("93");

        lightOff.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        lightOn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }
}
