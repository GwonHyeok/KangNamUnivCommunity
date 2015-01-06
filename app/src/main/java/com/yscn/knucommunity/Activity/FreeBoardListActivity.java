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
import com.yscn.knucommunity.Items.FreeBoardListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.FreeBoardListAdapter;

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
            getWindow().setStatusBarColor(getResources().getColor(R.color.freeboard_main_color));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(getString(R.string.community_freeboard_title));
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
        startActivity(new Intent(this, FreeBoardDetailActivity.class));
    }
}
