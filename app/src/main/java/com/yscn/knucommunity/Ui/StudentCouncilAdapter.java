package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Items.StudentCouncilListItems;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */

public class StudentCouncilAdapter extends PagerAdapter {

    private final String[] TITLES = {"여울림", "끌림"};
    private Context mContext;

    /* 학생회 정보 관련 데이터 */
    private HashMap<String, ArrayList<StudentCouncilListItems>> infoMap;

    public StudentCouncilAdapter(Context context) {
        this.mContext = context;
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
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        ListView listView = new ListView(mContext);
        listView.setBackgroundColor(0xFFF5F5F5);
        ArrayList<StudentCouncilListItems> itemses = new ArrayList<StudentCouncilListItems>();
        switch (position) {
            case 0:
                itemses = infoMap.get("riffle");
                break;
            case 1:
                itemses = infoMap.get("drag");
                break;
        }
        listView.setAdapter(new StudentCouncilListAdapter(mContext, R.layout.ui_studentcouncillist, itemses));
        viewGroup.addView(listView);
        return listView;
    }

    @Override
    public void destroyItem(ViewGroup pager, int position, Object view) {
        pager.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    public void setInfoMap(HashMap<String, ArrayList<StudentCouncilListItems>> infoMap) {
        this.infoMap = infoMap;
    }
}