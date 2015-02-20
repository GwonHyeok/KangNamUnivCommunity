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
        view = LayoutInflater.from(mContext).inflate(R.layout.activity_shop_help, viewGroup, false);
        TextView textView = (TextView) view.findViewById(R.id.shop_help_info_text);
        switch (position) {
            case 0:
                textView.setText("장터를 이용하시기 전에, 몇가지 주의사항을\n 반드시 숙지해 주세요.");
                break;
            case 1:
                textView.setText("장터 기능에서는 안전한 거래를 위해 실명제를 사용하고 있습니다.");
                break;
            case 2:
                textView.setText("직거래를 통해 거래하시기 바라며 만약 사기피해를 당했을 경우 경찰서에 신고하시기 바랍니다.");
                break;
            case 3:
                textView.setText("본인의 부주의로 인한 피해는 책임지지 않으니 꼭 주의하시기 바랍니다.");
                break;
        }
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
