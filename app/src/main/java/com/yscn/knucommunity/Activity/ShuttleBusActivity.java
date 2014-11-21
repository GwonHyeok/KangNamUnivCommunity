package com.yscn.knucommunity.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class ShuttleBusActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        viewInit();
        setContentView(R.layout.activity_shuttlebus);

        ViewPager viewPager = (ViewPager) findViewById(R.id.shuttlebus_viewpager);
        viewPager.setAdapter(new ShuttleBusAdpater());
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFFE8E8E8);
        }
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_center_base, null);
        ((ImageView) view.findViewById(R.id.actionbar_center_base_image)).setImageResource(R.drawable.ic_nav_blue_back);
        ((TextView) view.findViewById(R.id.actionbar_center_base_title)).setText("달구지");
        ((TextView) view.findViewById(R.id.actionbar_center_base_title)).setTextColor(0xFF455A64);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFE8E8E8")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
    }

    private class ShuttleBusAdpater extends PagerAdapter {

        private String[] TITLE = new String[]{"이공관", "기흥역"};


        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            TextView textView = new TextView(ShuttleBusActivity.this);
            textView.setText(TITLE[position]);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(0xFF37474F);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            viewGroup.addView(textView);
            return textView;
        }

        @Override
        public int getCount() {
            return TITLE.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup pager, int position, Object view) {
            pager.removeView((View) view);
        }
    }
}
