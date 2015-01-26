package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class CommunittyActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_community);

        getSupportActionBar().hide();
        findViewById(R.id.communitty_free_board).setOnClickListener(this);
        findViewById(R.id.communitty_faq_board).setOnClickListener(this);
        findViewById(R.id.communitty_greenright_board).setOnClickListener(this);
        findViewById(R.id.communitty_metting_board).setOnClickListener(this);
        findViewById(R.id.communitty_delivery_food).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.communitty_metting_board) {
            startActivity(new Intent(this, MeetingActivity.class));
        } else if (id == R.id.communitty_greenright_board) {
            startActivity(new Intent(this, GreenLightListActivity.class));
        } else if (id == R.id.communitty_faq_board) {
            startActivity(new Intent(this, FaqListActivity.class));
        } else if (id == R.id.communitty_delivery_food) {
            startActivity(new Intent(this, DeliveryFoodActivity.class));
        } else if (id == R.id.communitty_free_board) {
            startActivity(new Intent(this, FreeBoardListActivity.class));
        }
        finish();
    }
}
