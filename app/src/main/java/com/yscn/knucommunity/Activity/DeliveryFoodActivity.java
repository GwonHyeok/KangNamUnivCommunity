package com.yscn.knucommunity.Activity;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.Items.DeliveryListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.DeliveryListAdapter;
import com.yscn.knucommunity.Ui.DeliverySpinnerAdapter;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class DeliveryFoodActivity extends MenuBaseActivity {
    private DeliverySpinnerAdapter deliverySpinnerAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_deliveryfood);

        actionBarInit();
        viewInit();
    }

    private void viewInit() {
        ListView listView = (ListView) findViewById(R.id.delivery_listview);
        ArrayList<DeliveryListItems> itemses = new ArrayList<>();
        itemses.add(new DeliveryListItems("", ""));
        itemses.add(new DeliveryListItems("", ""));
        itemses.add(new DeliveryListItems("", ""));
        listView.setAdapter(new DeliveryListAdapter(this, R.layout.ui_deliveryfood_card, itemses));
        listView.setDividerHeight(0);

        final Spinner spinner = (Spinner) findViewById(R.id.delivery_spinner);
        String[] deliveryList = getResources().getStringArray(R.array.delivery_list);
        deliverySpinnerAdapter = new DeliverySpinnerAdapter(this, R.layout.ui_deliveryspinner, deliveryList);
        spinner.setAdapter(deliverySpinnerAdapter);
        spinner.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (Build.VERSION.SDK_INT >= 16) {
                    spinner.setDropDownVerticalOffset(spinner.getHeight());
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Change Food Items

                // Set Current Position cause change color selected Item
                deliverySpinnerAdapter.setCurrentPosition(position);

                // May Be Some Phone occur spinner drop down button color
                // white to black when click Other Item
                spinner.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });

        /* Spinner DropDown Button Color Change */
        spinner.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);
    }

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.delivery_main_color));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
        getMenuInflater().inflate(R.menu.delivery_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
