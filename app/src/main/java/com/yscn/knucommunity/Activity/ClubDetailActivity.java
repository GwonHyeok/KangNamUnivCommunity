package com.yscn.knucommunity.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class ClubDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_clubdetail);
        viewInit();
    }

    private void viewInit() {
        /* Hide Action Bar */
        getSupportActionBar().hide();
    }
}
