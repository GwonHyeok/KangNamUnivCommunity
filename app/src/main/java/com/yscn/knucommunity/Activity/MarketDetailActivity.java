package com.yscn.knucommunity.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yscn.knucommunity.Items.MarketDetailListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.MarketDetailListAdapter;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MarketDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_shop_detail);
        actionBarInit();
        ListView listView = (ListView) findViewById(R.id.shop_detail_list);
        ArrayList<MarketDetailListItems> objects = new ArrayList<MarketDetailListItems>();
        objects.add(new MarketDetailListItems("", "", "", 0));
        objects.add(new MarketDetailListItems("", "", "", 0));
        objects.add(new MarketDetailListItems("", "", "", 0));
        listView.setAdapter(new MarketDetailListAdapter(this, R.layout.ui_marketillist, objects));
    }

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF43a047);
        }

        /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_community_base, null);
        ((TextView) view.findViewById(R.id.actionbar_community_base_title)).setText("장터");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43a047")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
    }
}
