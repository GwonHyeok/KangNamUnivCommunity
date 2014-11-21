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
import com.yscn.knucommunity.Ui.FreeBoardListAdapter;
import com.yscn.knucommunity.Items.FreeBoardListItems;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FreeBoardListActivity extends MenuBaseActivity implements AdapterView.OnItemClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_freeboard_list);
        actionBarInit();
        viewInit();
    }

    private void viewInit() {
        ArrayList<FreeBoardListItems> items = new ArrayList<FreeBoardListItems>();
        items.add(new FreeBoardListItems("", "", "", 0));
        items.add(new FreeBoardListItems("", "", "", 0));
        items.add(new FreeBoardListItems("", "", "", 0));
        ListView freeBoardListView = (ListView) findViewById(R.id.freeboard_list);
        freeBoardListView.setOnItemClickListener(this);
        freeBoardListView.setAdapter(new FreeBoardListAdapter(this, R.layout.ui_freeboardillist, items));
    }

    private void actionBarInit() {
        /* Lollipop ActionBar */
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xffffbc00);
        }

         /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_community_base, null);
        ((TextView) view.findViewById(R.id.actionbar_community_base_title)).setText("자유개시판");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffbc00")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(this, FreeBoardDetailActivity.class));
    }
}
