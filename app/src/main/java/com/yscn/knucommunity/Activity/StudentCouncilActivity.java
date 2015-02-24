package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.astuetz.PagerSlidingTabStrip;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.StudentCouncilAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;


/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class StudentCouncilActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private PagerSlidingTabStrip tabStrip;
    private ViewPager viewPager;
    private StudentCouncilAdapter councilAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_studentcouncil);
        viewInit();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void viewInit() {

        /* ActionBar Init */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("총 여학생회");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* ViewPager Init */
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.student_council_tabs);
        viewPager = (ViewPager) findViewById(R.id.student_council_viewpager);
        tabStrip.setTypeface(ApplicationUtil.getInstance().getTypeFace(Typeface.NORMAL), Typeface.NORMAL);
        tabStrip.setOnPageChangeListener(this);
        councilAdapter = new StudentCouncilAdapter(getSupportFragmentManager());
        viewPager.setAdapter(councilAdapter);
        tabStrip.setViewPager(viewPager);

        /* RiffleMap Icon */
        findViewById(R.id.student_council_riffle_icon).setOnClickListener(this);
    }

    private Context getContext() {
        return StudentCouncilActivity.this;
    }

    /*
     *  screenWidth : viewPagerPosition = (fabWidth + fabMarginWidth) : x
     *  (fabWidth + fabMarginWidth) * position = screenWidth * x;
     *  x = (fabWidth + fabMarginWidth) * position / screenWidth;
     */
    @Override
    public void onPageScrolled(int i, float v, int i2) {
        final ImageButton riffleIcon = (ImageButton) findViewById(R.id.student_council_riffle_icon);
//        if (i != 1 || i2 != 0) {
//            int fabWidth = getResources().getDimensionPixelOffset(R.dimen.floating_action_button_width);
//            int fabMarginWidth = getResources().getDimensionPixelOffset(R.dimen.floating_action_button_margin);
//            int screenWidth = getResources().getDisplayMetrics().widthPixels;
//            riffleIcon.setTranslationX(-((fabWidth + fabMarginWidth) * i2 / screenWidth));
//        }
    }

    @Override
    public void onPageSelected(int i) {
        ImageButton riffleIcon = (ImageButton) findViewById(R.id.student_council_riffle_icon);
        switch (i) {
            case 0:
                riffleIcon.setVisibility(View.GONE);
                break;
            case 1:
                riffleIcon.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.student_council_riffle_icon) {
            startActivity(new Intent(this, RiffleMapActivity.class));
        }
    }
}