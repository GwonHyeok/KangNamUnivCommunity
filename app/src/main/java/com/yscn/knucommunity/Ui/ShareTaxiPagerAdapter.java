package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by GwonHyeok on 15. 1. 20..
 */
public class ShareTaxiPagerAdapter extends PagerAdapter {
    private Context mContext;
    private String[][] mDay;

    public ShareTaxiPagerAdapter(Context context, String[][] day) {
        this.mContext = context;
        this.mDay = day;
    }

    @Override
    public int getCount() {
        return mDay.length;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.WHITE);
        textView.setText(String.valueOf(mDay[position][1]));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);
        viewGroup.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(ViewGroup pager, int position, Object view) {
        pager.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
