package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.Items.GreenLightListItems;
import com.yscn.knucommunity.R;
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
            getWindow().setStatusBarColor(getResources().getColor(R.color.greenlight_main_color));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(getString(R.string.community_greenlight_title));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_nav_menu_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(this, GreenLightDetailActivity.class));
    }
}
