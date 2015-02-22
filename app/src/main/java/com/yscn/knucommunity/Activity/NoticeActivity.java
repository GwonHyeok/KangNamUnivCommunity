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

import com.astuetz.PagerSlidingTabStrip;
import com.nineoldandroids.view.ViewHelper;
import com.yscn.knucommunity.CustomView.BaseNavigationDrawerActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.KenBurnsSupportView;
import com.yscn.knucommunity.CustomView.NoticeListFragment;
import com.yscn.knucommunity.CustomView.ScrollTabHolder;
import com.yscn.knucommunity.CustomView.ScrollTabHolderFragment;
import com.yscn.knucommunity.Items.NoticeItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NoticeActivity extends BaseNavigationDrawerActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener {

    private final String[] TITLES = {"공지사항", "학사제도", "장학제도"};
    private View mHeader;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private int mActionBarHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
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

        attatchView(R.layout.activity_notice);

        KenBurnsSupportView mHeaderPicture = (KenBurnsSupportView) findViewById(R.id.notice_header_picture);
        mHeaderPicture.setResourceIds(R.drawable.bg_notice_1, R.drawable.bg_notice_2, R.drawable.bg_notice_3, R.drawable.bg_notice_4);
        mHeader = findViewById(R.id.header);

        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.notice_tabs);
        mViewPager = (ViewPager) findViewById(R.id.notice_viewpager);
        mViewPager.setOffscreenPageLimit(3);

        getNoticeData();
        mPagerSlidingTabStrip.setOnPageChangeListener(this);
        mPagerSlidingTabStrip.setTypeface(ApplicationUtil.getInstance().getTypeFace(Typeface.BOLD), Typeface.BOLD);
        mToolbar.setTitleTextColor(0xffffffff);
        mToolbar.setTitle(TITLES[0]);

        ApplicationUtil.getInstance().setTypeFace(findViewById(R.id.notice_root));
    }

    private void getNoticeData() {
        new AsyncTask<Void, Void, HashMap<String, ArrayList<NoticeItems>>>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                boolean isOnline = ApplicationUtil.getInstance().isOnlineNetwork();
                if (!isOnline) {
                    cancel(true);
                    AlertToast.error(getApplicationContext(), R.string.error_check_network_state);
                    finish();
                    return;
                }
                clearProgressDialog = new ClearProgressDialog(NoticeActivity.this);
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
                if (itemes == null) {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                    finish();
                    return;
                }
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
        mToolbar.setTitle(TITLES[position]);
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
        mToolbar.setAlpha(alpha);
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