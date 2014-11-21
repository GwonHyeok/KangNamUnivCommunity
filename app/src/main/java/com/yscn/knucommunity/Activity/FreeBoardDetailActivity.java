package com.yscn.knucommunity.Activity;

import android.content.Intent;
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
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FreeBoardDetailActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_freeboarddetail);
        findViewById(R.id.freeboard_replayview).setOnClickListener(this);
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

        view.findViewById(R.id.actionbar_community_base_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FreeBoardDetailActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.freeboard_replayview) {
            startActivity(new Intent(this, FreeBoardReplyActivity.class));
        }
    }
}
