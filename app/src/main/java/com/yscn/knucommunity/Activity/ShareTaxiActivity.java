package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.ShareTaxiPagerAdapter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 1. 20..
 */
public class ShareTaxiActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private String mDate[][];
    private String mYear[];
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_sharetaxi);
        toolbarInit();

        // Adapter 에서 필요로 하는 날자 정보를 저장해놓음
        setDayArray();

        // Viewpager init
        viewPager = (ViewPager) findViewById(R.id.share_taxi_viewpager);
        viewPager.setAdapter(new ShareTaxiPagerAdapter(this, mDate));
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(this);

        // 상단바 투명 KITKAT 이상부터
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        findViewById(R.id.share_taxi_nextday).setOnClickListener(this);
        findViewById(R.id.share_taxi_yesterday).setOnClickListener(this);

        setTaxiData();
    }

    private void setTaxiData() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.share_taxi_data_view);

        for (int i = 0; i < 4; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.ui_sharetaxilist, linearLayout, false);
            view.findViewById(R.id.share_taxi_start_locaction_textview).setSelected(true);
            view.findViewById(R.id.share_taxi_stop_locaction_textview).setSelected(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShareTaxiActivity.this, ShareTaxiDetailActivity.class);
                    intent.putExtra("writerStudentNumber", "201401239");
                    startActivity(intent);
                }
            });
            view.setBackgroundResource(R.drawable.bg_default_select_item_effect);
            linearLayout.addView(view);
        }
    }

    /*
     *        월    일
     *      ["1"]["27"] : [00] [01]
     *      ["1"]["28"] : [10] [11]
     *      ["2"]["01"] : [20] [21]
     *      ["2"]["02"] : [30] [31]
     */
    private void setDayArray() {
        mDate = new String[4][2];
        mYear = new String[4];
        Date now_date = Calendar.getInstance().getTime();

        // 하루 전날로 돌린다
        now_date.setTime(now_date.getTime() - 1000 * 60 * 60 * 24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now_date);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    mDate[i][j] = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                } else if (j == 1) {
                    mDate[i][j] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                }
            }
            mYear[i] = String.valueOf(calendar.get(Calendar.YEAR));

            // 다음날자로 돌린다.
            now_date.setTime(now_date.getTime() + 1000 * 60 * 60 * 24);
            calendar.setTime(now_date);
        }
    }

    private void toolbarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TextView dateView = (TextView) findViewById(R.id.share_taxi_date_textview);
        dateView.setText(
                String.format(getString(R.string.sharetaxi_time_format),
                        mDate[position][0],
                        mYear[position])
        );
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.share_taxi_nextday) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        } else if (id == R.id.share_taxi_yesterday) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        }
    }
}
