package com.yscn.knucommunity.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class FaqDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_faqdetail);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.fatdetail_main_scroll_activity);
        for (int i = 0; i < 2; i++) {
            View replyView = LayoutInflater.from(this).inflate(R.layout.ui_faqreply, null);
            linearLayout.addView(replyView);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) replyView.getLayoutParams();
            params.setMargins(0, 0, 0, 10);
            replyView.setLayoutParams(params);
        }

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
}
