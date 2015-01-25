package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.MarketAdapter;

import java.util.Random;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MarketMainActivity extends ActionBarActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_shop_main);
        viewInit();
    }

    private void viewInit() {
        Random random = new Random();
        int i = random.nextInt(3);
        View view = findViewById(R.id.shop_main);
        if (i == 0) {
            view.setBackgroundResource(R.drawable.bg_shop_1);
        } else if (i == 1) {
            view.setBackgroundResource(R.drawable.bg_shop_2);
        } else if (i == 2) {
            view.setBackgroundResource(R.drawable.bg_shop_3);
        }

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewPager pager = (ViewPager) findViewById(R.id.shop_viewpager);
        findViewById(R.id.shop_move_detail).setOnClickListener(this);
        pager.setAdapter(new MarketAdapter(this));
        pager.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.shop_move_detail) {
            finish();
            startActivity(new Intent(this, MarketListActivity.class));
        }

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        ImageView positionImageView = (ImageView) findViewById(R.id.shop_main_scroll_position);
        switch (i) {
            case 0:
                positionImageView.setImageResource(R.drawable.ic_shop_help_all_1);
                break;
            case 1:
                positionImageView.setImageResource(R.drawable.ic_shop_help_all_2);
                break;
            case 2:
                positionImageView.setImageResource(R.drawable.ic_shop_help_all_3);
                break;
            case 3:
                positionImageView.setImageResource(R.drawable.ic_shop_help_all_4);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
