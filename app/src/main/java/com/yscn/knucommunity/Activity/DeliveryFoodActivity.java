package com.yscn.knucommunity.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.DeliveryListAdapter;
import com.yscn.knucommunity.Items.DeliveryListItems;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class DeliveryFoodActivity extends MenuBaseActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        actionBarInit();
        ListView listView = new ListView(this);
        ArrayList<DeliveryListItems> itemses = new ArrayList<DeliveryListItems>();
        itemses.add(new DeliveryListItems("", ""));
        itemses.add(new DeliveryListItems("", ""));
        itemses.add(new DeliveryListItems("", ""));
        listView.setAdapter(new DeliveryListAdapter(this, R.layout.ui_deliveryfood_card, itemses));
        listView.setDividerHeight(0);
        setContentView(listView);
    }

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0XffEF6C00);
        }

        /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_community_base, null);
        ((TextView) view.findViewById(R.id.actionbar_community_base_title)).setText("패스트푸드");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EF6C00")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);

    }

}
