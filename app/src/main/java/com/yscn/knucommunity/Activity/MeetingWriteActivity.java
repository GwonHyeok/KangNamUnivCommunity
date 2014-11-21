package com.yscn.knucommunity.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class MeetingWriteActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_meeting_write);
        actionBarInit();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xffd7335b);
        }
    }

    private void actionBarInit() {
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_center_base, null);
        TextView titleView = (TextView) view.findViewById(R.id.actionbar_center_base_title);
        ImageView backView = (ImageView) view.findViewById(R.id.actionbar_center_base_image);
        titleView.setText("미팅");
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeetingWriteActivity.this.finish();
            }
        });

        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d7335b")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
    }
}
