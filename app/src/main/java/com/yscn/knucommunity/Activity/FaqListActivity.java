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
import com.yscn.knucommunity.Ui.FaqListAdapter;
import com.yscn.knucommunity.Items.FaqListItems;

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
            getWindow().setStatusBarColor(0xFFe64a19);
        }

        /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_community_base, null);
        ((TextView) view.findViewById(R.id.actionbar_community_base_title)).setText("질문과 답변");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e64a19")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(this, FaqDetailActivity.class));
    }
}
