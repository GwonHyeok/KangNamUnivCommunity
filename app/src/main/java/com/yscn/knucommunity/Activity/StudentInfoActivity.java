package com.yscn.knucommunity.Activity;

import android.os.Bundle;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class StudentInfoActivity extends MenuBaseActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_studentinfo);
        getSupportActionBar().hide();
    }
}
