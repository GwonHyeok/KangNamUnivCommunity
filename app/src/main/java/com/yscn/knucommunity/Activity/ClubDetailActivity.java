package com.yscn.knucommunity.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.yscn.knucommunity.R;

import java.util.Random;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class ClubDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_clubdetail);
        viewInit();
        setRandomColor();
    }

    private void viewInit() {
        /* Hide Action Bar */
        getSupportActionBar().hide();
    }

    private void setRandomColor() {
        Random random = new Random();
        int index = random.nextInt(7);

        int[] primarydark = getResources().getIntArray(R.array.background_dark_studentground_detail_list);
        int[] background = getResources().getIntArray(R.array.background_studentground_detail_list);
        int[] highlight = getResources().getIntArray(R.array.highlight_studentground_detail_list);
        int[] dull = getResources().getIntArray(R.array.dull_studentground_detail_list);

        /* Background Color */
        View bg_view = findViewById(R.id.relativeLayout2);
        bg_view.setBackgroundColor(background[index]);

        /* Highlight Color */
        TextView title = (TextView) findViewById(R.id.textView3);
        View line_view = findViewById(R.id.textView2);
        title.setTextColor(highlight[index]);
        line_view.setBackgroundColor(highlight[index]);

        /* Dull Color */
        TextView homepageView = (TextView) findViewById(R.id.textView5);
        TextView infoView = (TextView) findViewById(R.id.textView4);
        View line_view2 = findViewById(R.id.view);
        homepageView.setTextColor(dull[index]);
        line_view2.setBackgroundColor(dull[index]);
        infoView.setTextColor(dull[index]);

        /* set Status Bar Color */
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(primarydark[index]);
            getWindow().setNavigationBarColor(primarydark[index]);
        }
    }
}
