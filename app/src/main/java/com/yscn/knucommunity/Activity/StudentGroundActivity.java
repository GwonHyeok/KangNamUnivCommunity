package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;

import java.util.Random;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class StudentGroundActivity extends MenuBaseActivity implements View.OnClickListener {
    private final String TAG = "StudentGroundActivity";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_studentground);
        viewInit();
    }

    private void viewInit() {

        /* Set Background */
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.studentground_mainview);
        Random random = new Random();
        int i = random.nextInt(2);
        if (i == 0) {
            linearLayout.setBackgroundResource(R.drawable.bg_stndentground_1);
        } else {
            linearLayout.setBackgroundResource(R.drawable.bg_studentground_2);
        }
        /* remove ActionBar */
        getSupportActionBar().hide();

        /* Resize view Child From Scrollview  */
        final ScrollView scrollView = (ScrollView) findViewById(R.id.studentgroup_scrollview);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LinearLayout layout_line1 = (LinearLayout) findViewById(R.id.studentgroup_line1);
                LinearLayout layout_line2 = (LinearLayout) findViewById(R.id.studentgroup_line2);
                LinearLayout layout_line3 = (LinearLayout) findViewById(R.id.studentgroup_line3);
                int mainHeight = scrollView.getHeight();

                ViewGroup.LayoutParams params = layout_line1.getLayoutParams();
                params.height = mainHeight / 2;
                layout_line1.setLayoutParams(params);

                params = layout_line2.getLayoutParams();
                params.height = mainHeight / 2;
                layout_line2.setLayoutParams(params);

                params = layout_line3.getLayoutParams();
                params.height = mainHeight / 2;
                layout_line3.setLayoutParams(params);

                if (Build.VERSION.SDK_INT >= 16) {
                    scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        /* set ClickListener */
        findViewById(R.id.studentground_major).setOnClickListener(this);
        findViewById(R.id.studentground_club).setOnClickListener(this);
        findViewById(R.id.studentground_restraunt).setOnClickListener(this);
        findViewById(R.id.studentground_council).setOnClickListener(this);
        findViewById(R.id.studentground_shuttlebus).setOnClickListener(this);
        findViewById(R.id.studentground_library).setOnClickListener(this);
        findViewById(R.id.studentground_taxi).setOnClickListener(this);
        findViewById(R.id.open_menu).setOnClickListener(this);
        findViewById(R.id.studentground_map).setOnClickListener(this);
    }

    private void log(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.studentground_major) {
            startActivity(new Intent(this, MajorInfoActivity.class));
        } else if (id == R.id.studentground_club) {
            startActivity(new Intent(this, ClubInfoActivity.class));
        } else if (id == R.id.studentground_restraunt) {
            startActivity(new Intent(this, SchoolRestrauntActivity.class));
        } else if (id == R.id.studentground_council) {
            startActivity(new Intent(this, StudentCouncilActivity.class));
        } else if (id == R.id.studentground_shuttlebus) {
            startActivity(new Intent(this, ShuttleBusActivity.class));
        } else if (id == R.id.studentground_library) {
            startActivity(new Intent(this, LibraryActivity.class));
        } else if (id == R.id.studentground_map) {
            startActivity(new Intent(this, CampusMapActivity.class));
        } else if (id == R.id.studentground_taxi) {
            startActivity(new Intent(this, ShareTaxiActivity.class));
        }
    }

}
