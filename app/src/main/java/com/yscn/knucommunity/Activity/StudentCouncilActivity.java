package com.yscn.knucommunity.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.PagerSlidingTabStrip;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.StudentCouncilAdapter;
import com.yscn.knucommunity.Items.StudentCouncilListItems;
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
            getWindow().setStatusBarColor(0xFF0D47A1);
            getWindow().setNavigationBarColor(0xFF0D47A1);
        }
        /* ActionBar Init */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_base, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF0D47A1));
        actionBar.setElevation(0);
        ((TextView) view.findViewById(R.id.actionbar_base_title)).setText("학생회");
        view.findViewById(R.id.actionbar_base_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentCouncilActivity.this.finish();
            }
        });

        /* ViewPager Init */
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.student_council_tabs);
        viewPager = (ViewPager) findViewById(R.id.student_council_viewpager);
        tabStrip.setOnPageChangeListener(this);

        /* RiffleMap Icon */
        findViewById(R.id.student_council_riffle_icon).setOnClickListener(this);
    }

    private void getCouncilData() {
        new AsyncTask<Void, Void, HashMap<String, ArrayList<StudentCouncilListItems>>>() {
            private ProgressDialog dialog;
            private Context mContext = getContext();

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(StudentCouncilActivity.this);
                dialog.setTitle("제목");
                dialog.setMessage("메세지");
                dialog.setIndeterminate(true);
                dialog.show();
            }

            @Override
            protected HashMap<String, ArrayList<StudentCouncilListItems>> doInBackground(Void... voids) {
                try {
                    return NetworkUtil.getInstance().getCouncilInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
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