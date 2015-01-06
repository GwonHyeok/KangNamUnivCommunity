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
import com.yscn.knucommunity.Items.FaqListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.FaqListAdapter;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FaqListActivity extends MenuBaseActivity implements AdapterView.OnItemClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_faq_list);
        actionBarInit();
        viewInit();
    }

    private void viewInit() {
        ArrayList<FaqListItems> items = new ArrayList<FaqListItems>();
        items.add(new FaqListItems("", "", "", 0));
        items.add(new FaqListItems("", "", "", 0));
        items.add(new FaqListItems("", "", "", 0));
        ListView faqListView = (ListView) findViewById(R.id.faq_list);
        faqListView.setOnItemClickListener(this);
        faqListView.setAdapter(new FaqListAdapter(this, R.layout.ui_faqlist, items));
    }

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.faq_main_color));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(getString(R.string.community_faq_title));
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
        startActivity(new Intent(this, FaqDetailActivity.class));
    }
}
