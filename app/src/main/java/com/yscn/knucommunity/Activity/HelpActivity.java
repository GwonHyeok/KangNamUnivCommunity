package com.yscn.knucommunity.Activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 28..
 */
public class HelpActivity extends ActionBarActivity {
    private static String[] helperTexts;
    private ViewPager mViewPager;
    private HelpPagerAdapter mHelpPagerAdapter;
    private TextView mPrevButton, mNextButton;
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_help);
        helperTexts = getResources().getStringArray(R.array.help_text_array);
        viewInit();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void viewInit() {
        mViewPager = (ViewPager) findViewById(R.id.help_viewpager);
        mPrevButton = (TextView) findViewById(R.id.help_prev_button);
        mNextButton = (TextView) findViewById(R.id.help_next_button);
        mImageView = (ImageView) findViewById(R.id.help_imageview);

        mHelpPagerAdapter = new HelpPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mHelpPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mPrevButton.setVisibility(View.GONE);
                } else {
                    mPrevButton.setVisibility(View.VISIBLE);
                }
                if (position == mHelpPagerAdapter.getCount() - 1) {
                    mNextButton.setText("끝");
                } else {
                    mNextButton.setText("다음");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mViewPager.getCurrentItem();
                if (currentPosition - 1 != -1) {
                    mViewPager.setCurrentItem(currentPosition - 1, true);
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxCount = mHelpPagerAdapter.getCount();
                if (maxCount == mViewPager.getCurrentItem() + 1) {
                    finish();
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            }
        });

        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int viewWidth = mImageView.getWidth();
                int viewHeight = mImageView.getHeight();
                Bitmap bitmap = ApplicationUtil.getInstance().decodeSampledBitmap(
                        getResources(),
                        R.drawable.bg_help,
                        viewWidth,
                        viewHeight);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                mImageView.setImageDrawable(bitmapDrawable);

                if (Build.VERSION.SDK_INT >= 16) {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* 화면이 끝나면 isneedshowhelp 값을 false 로 변경 다시는 뜨지 않도록 */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isneedshowhelp", false);
        editor.apply();
    }

    public static class HelpFragment extends Fragment {
        private int mPosition;

        static HelpFragment newInstance(int position) {
            HelpFragment f = new HelpFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            f.setArguments(bundle);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPosition = getArguments().getInt("position", -1);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            TextView view = new TextView(container.getContext());
            viewInit(view);
            return view;
        }

        private void viewInit(TextView view) {
            int textviewPadding = (int) ApplicationUtil.getInstance().dpToPx(20);
            view.setText(helperTexts[mPosition]);
            view.setPadding(textviewPadding, textviewPadding, textviewPadding, 0);
            view.setBackgroundColor(0xfff5f5f5);
            ApplicationUtil.getInstance().setTypeFace(view);
        }
    }

    private class HelpPagerAdapter extends FragmentPagerAdapter {

        public HelpPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return HelpFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return helperTexts.length;
        }
    }
}
