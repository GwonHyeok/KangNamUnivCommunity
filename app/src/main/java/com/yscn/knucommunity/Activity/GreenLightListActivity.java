package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Items.GreenLightListItems;
import com.yscn.knucommunity.Ui.GreenLightlListAdapter;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class GreenLightListActivity extends MenuBaseActivity implements AdapterView.OnItemClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_greenlight_list);
        actionBarInit();
        viewInit();
    }

    private void viewInit() {
        ArrayList<GreenLightListItems> items = new ArrayList<GreenLightListItems>();
        items.add(new GreenLightListItems("", "", 0));
        items.add(new GreenLightListItems("", "", 0));
        items.add(new GreenLightListItems("", "", 0));
        ListView greenLightListView = (ListView) findViewById(R.id.greenlight_list);
        greenLightListView.setOnItemClickListener(this);
        greenLightListView.setAdapter(new GreenLightlListAdapter(this, R.layout.ui_greenlightlsit, items));
    }

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0XffAD1457);
        }

        /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_community_base, null);
        ((TextView) view.findViewById(R.id.actionbar_community_base_title)).setText("그린라이트");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#AD1457")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(this, GreenLightDetailActivity.class));
    }
}
