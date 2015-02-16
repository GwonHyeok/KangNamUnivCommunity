package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MarketAdapter extends PagerAdapter {
    private Context mContext;

    public MarketAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        View view = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.activity_shop_help, null);
        TextView textView = (TextView) view.findViewById(R.id.shop_help_info_text);
        textView.setText("장터를 이용하시기 전에, 몇가지 주의사항을\n 반드시 숙지해 주세요.");
        viewGroup.addView(view);
        ApplicationUtil.getInstance().setTypeFace(view);
        return view;
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
