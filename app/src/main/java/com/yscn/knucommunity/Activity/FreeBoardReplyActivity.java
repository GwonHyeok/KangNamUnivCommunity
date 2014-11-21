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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class FreeBoardReplyActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_freeboardreply);
        actionBarInit();

        findViewById(R.id.freeboard_reply_mainview).setOnClickListener(this);

        LinearLayout mainView = (LinearLayout) findViewById(R.id.freeboard_reply_scrollview);
        for (int i = 0; i < 5; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.ui_freeboardreply, null);
            mainView.addView(view);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
            params.setMargins(0, 0, 0, 10);
            view.setLayoutParams(params);
        }
    }

    private void actionBarInit() {
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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.freeboard_reply_mainview) {
            RelativeLayout replySpace = (RelativeLayout) findViewById(R.id.freeboard_replyspace);
            if (replySpace.isShown()) {
                replySpace.setVisibility(View.GONE);
                replySpace.startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_to_left));
            } else {
                replySpace.setVisibility(View.VISIBLE);
                replySpace.startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_to_right));
            }
        }
    }
}
