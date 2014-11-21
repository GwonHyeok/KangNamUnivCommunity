package com.yscn.knucommunity.CustomView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yscn.knucommunity.Activity.CommunittyActivity;
import com.yscn.knucommunity.Activity.LinkActivity;
import com.yscn.knucommunity.Activity.MarketMainActivity;
import com.yscn.knucommunity.Activity.NoticeActivity;
import com.yscn.knucommunity.Activity.StudentGroundActivity;
import com.yscn.knucommunity.Activity.StudentInfoActivity;
import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class MenuBaseActivity extends ActionBarActivity implements View.OnClickListener {
    public SlidingMenu menu;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        slidingMenuInit();
    }

    protected void slidingMenuInit() {
        /* Measure Screen Size, Define Sliding Menu Width */
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int squareSize;
        squareSize = width - (height / 6);
         /* Add Sliding Menu */
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setBehindOffset(squareSize);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidingmenumain);

        /*
         * menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
         * menu.setShadowDrawable(R.drawable.shadow);
         * menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
         */

        /* Menu Click Listener */
        menu.findViewById(R.id.slidingmenu_studentinfo).setOnClickListener(this);
        menu.findViewById(R.id.slidingmenu_studentground).setOnClickListener(this);
        menu.findViewById(R.id.slidingmenu_community).setOnClickListener(this);
        menu.findViewById(R.id.slidingmenu_link).setOnClickListener(this);
        menu.findViewById(R.id.slidingmenu_market).setOnClickListener(this);
        menu.findViewById(R.id.slidingmenu_notice).setOnClickListener(this);
    }

    protected void openSlidingMenu() {
        menu.showMenu();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.slidingmenu_studentground) {
            startNewActivity(new Intent(this, StudentGroundActivity.class));
        } else if (id == R.id.slidingmenu_notice) {
            startNewActivity(new Intent(this, NoticeActivity.class));
        } else if (id == R.id.slidingmenu_market) {
            startNewActivity(new Intent(this, MarketMainActivity.class));
        } else if (id == R.id.slidingmenu_link) {
            startNewActivity(new Intent(this, LinkActivity.class));
        } else if (id == R.id.slidingmenu_community) {
            startNewActivity(new Intent(this, CommunittyActivity.class));
        } else if (id == R.id.slidingmenu_studentinfo) {
            startNewActivity(new Intent(this, StudentInfoActivity.class));
        } else if (id == R.id.open_menu) {
            openSlidingMenu();
        }
    }

    private void startNewActivity(Intent intent) {
        finish();
        startActivity(intent);
    }
}
