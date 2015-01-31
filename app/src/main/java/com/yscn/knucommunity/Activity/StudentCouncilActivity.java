package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.PagerSlidingTabStrip;
import com.yscn.knucommunity.Items.StudentCouncilListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Ui.StudentCouncilAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


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
        getCouncilData();
    }

    private void viewInit() {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xff0277bd);
            getWindow().setNavigationBarColor(0xff0277bd);
        }

        /* ActionBar Init */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("학생회");
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
        tabStrip.setTypeface(null, Typeface.NORMAL);
        tabStrip.setOnPageChangeListener(this);

        /* RiffleMap Icon */
        findViewById(R.id.student_council_riffle_icon).setOnClickListener(this);
    }

    private void getCouncilData() {
        new AsyncTask<Void, Void, HashMap<String, ArrayList<StudentCouncilListItems>>>() {
            private ClearProgressDialog dialog;
            private Context mContext = getContext();

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    dialog = new ClearProgressDialog(StudentCouncilActivity.this);
                    dialog.show();
                } else {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(false);
                }
            }

            @Override
            protected HashMap<String, ArrayList<StudentCouncilListItems>> doInBackground(Void... voids) {
                try {
                    return NetworkUtil.getInstance().getCouncilInfo();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, ArrayList<StudentCouncilListItems>> dataSet) {
                dialog.cancel();
                councilAdapter = new StudentCouncilAdapter(mContext);
                if (dataSet != null) {
                    councilAdapter.setInfoMap(dataSet);
                } else {
                    // Error Occure
                    AlertToast.error(getContext(), R.string.error_to_work);
                }
                viewPager.setAdapter(councilAdapter);
                tabStrip.setViewPager(viewPager);
            }
        }.execute();
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
        if (i != 1 || i2 != 0) {
            int fabWidth = getResources().getDimensionPixelOffset(R.dimen.floating_action_button_width);
            int fabMarginWidth = getResources().getDimensionPixelOffset(R.dimen.floating_action_button_margin);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            riffleIcon.setTranslationX((fabWidth + fabMarginWidth) * i2 / screenWidth);
        }
    }

    @Override
    public void onPageSelected(int i) {
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