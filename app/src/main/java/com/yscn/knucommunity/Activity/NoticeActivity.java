package com.yscn.knucommunity.Activity;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.KenBurnsSupportView;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.CustomView.NoticeListFragment;
import com.yscn.knucommunity.CustomView.PagerSlidingTabStrip;
import com.yscn.knucommunity.CustomView.ScrollTabHolder;
import com.yscn.knucommunity.CustomView.ScrollTabHolderFragment;
import com.yscn.knucommunity.Items.NoticeItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NoticeActivity extends MenuBaseActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener, View.OnClickListener {

    private final String[] TITLES = {"공지사항", "학사제도", "장학제도"};
    private View mHeader;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private int mActionBarHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private TextView actionBarHeaderTitleView;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;

    private TypedValue mTypedValue = new TypedValue();

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.parallax_min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.parallax_header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();

        setContentView(R.layout.activity_notice);

        KenBurnsSupportView mHeaderPicture = (KenBurnsSupportView) findViewById(R.id.notice_header_picture);
        mHeaderPicture.setResourceIds(R.drawable.bg_notice_1, R.drawable.bg_notice_2, R.drawable.bg_notice_3, R.drawable.bg_notice_4);
        mHeader = findViewById(R.id.header);

        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.notice_tabs);
        mViewPager = (ViewPager) findViewById(R.id.notice_viewpager);
        mViewPager.setOffscreenPageLimit(3);

        getNoticeData();
        mPagerSlidingTabStrip.setOnPageChangeListener(this);
        mPagerSlidingTabStrip.setTypeface(null, Typeface.NORMAL);
        actionBarHeaderTitleView = (TextView) findViewById(R.id.notice_header_title);
        findViewById(R.id.open_menu).setOnClickListener(this);
    }

    private void getNoticeData() {
        new AsyncTask<Void, Void, HashMap<String, ArrayList<NoticeItems>>>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected HashMap<String, ArrayList<NoticeItems>> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getNoticeList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, ArrayList<NoticeItems>> itemes) {
                mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), itemes);
                mPagerAdapter.setTabHolderScrollingContent(NoticeActivity.this);
                mViewPager.setAdapter(mPagerAdapter);
                mPagerSlidingTabStrip.setViewPager(mViewPager);
                clearProgressDialog.cancel();
            }
        }.execute();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // nothing
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // nothing
    }

    @Override
    public void onPageSelected(int position) {
        actionBarHeaderTitleView.setText(TITLES[position]);
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mPagerAdapter.getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);

        currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper.getTranslationY(mHeader)));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mViewPager.getCurrentItem() == pagePosition) {
            int scrollY = getScrollY(view);
            ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
            float ratio = clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
            setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
        }
    }

    @Override
    public void adjustScroll(int scrollHeight) {
    }

    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mHeaderHeight;
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        } else {
            getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);
        }

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    private void setTitleAlpha(float alpha) {
        actionBarHeaderTitleView.setAlpha(alpha);
    }

    @Override
    public void onClick(View v) {
        openSlidingMenu();
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
        private ScrollTabHolder mListener;
        private HashMap<String, ArrayList<NoticeItems>> itemes;

        public PagerAdapter(FragmentManager fm, HashMap<String, ArrayList<NoticeItems>> objects) {
            super(fm);
            mScrollTabHolders = new SparseArrayCompat<>();
            itemes = objects;
        }

        public void setTabHolderScrollingContent(ScrollTabHolder listener) {
            mListener = listener;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) NoticeListFragment.newInstance(position, itemes);

            mScrollTabHolders.put(position, fragment);
            if (mListener != null) {
                fragment.setScrollTabHolder(mListener);
            }

            return fragment;
        }

        public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
            return mScrollTabHolders;
        }
    }
}