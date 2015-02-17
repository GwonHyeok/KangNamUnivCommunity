package com.yscn.knucommunity.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.BeatViewPagetAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 17..
 */
public class BeatActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mSlidingTab;
    private BeatViewPagetAdapter mViewPagerAdapter;
    private int mPagePosition;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_beat);
        setToolbar();
        mViewPager = (ViewPager) findViewById(R.id.beat_viewpager);
        mSlidingTab = (PagerSlidingTabStrip) findViewById(R.id.beat_slidingtab);
        mViewPagerAdapter = new BeatViewPagetAdapter(getSupportFragmentManager(), this);


        mViewPager.setAdapter(mViewPagerAdapter);
        mSlidingTab.setViewPager(mViewPager);
        mSlidingTab.setOnPageChangeListener(this);
        ApplicationUtil.getInstance().setTypeFace(findViewById(R.id.beat_root));
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setTitle(R.string.text_beat);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 만약 뷰페이저 페이지가 3(후기)또는 4(Q&A)일때 메뉴 생성
        if (mPagePosition == 3 || mPagePosition == 2) {
            getMenuInflater().inflate(R.menu.board_menu, menu);
        } else {
            menu.clear();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.mPagePosition = position;
        invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
