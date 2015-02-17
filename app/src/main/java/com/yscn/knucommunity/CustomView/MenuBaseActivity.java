package com.yscn.knucommunity.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yscn.knucommunity.Activity.CommunittyActivity;
import com.yscn.knucommunity.Activity.LinkActivity;
import com.yscn.knucommunity.Activity.MarketMainActivity;
import com.yscn.knucommunity.Activity.NoticeActivity;
import com.yscn.knucommunity.Activity.StudentGroundActivity;
import com.yscn.knucommunity.Activity.StudentInfoActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class MenuBaseActivity extends ActionBarActivity {
    public SlidingMenu menu;

    @Override
    public void onStart() {
        super.onStart();
        if (menu == null) {
            slidingMenuInit();
        }
    }

    protected void slidingMenuInit() {
        int width = ApplicationUtil.getInstance().getScreenWidth();
        int height = ApplicationUtil.getInstance().getScreenHeight();
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
        menu.findViewById(R.id.slidingmenu_studentinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), StudentInfoActivity.class));
            }
        });
        menu.findViewById(R.id.slidingmenu_studentground).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), StudentGroundActivity.class));
            }
        });
        menu.findViewById(R.id.slidingmenu_community).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), CommunittyActivity.class));
            }
        });
        menu.findViewById(R.id.slidingmenu_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), LinkActivity.class));
            }
        });
        menu.findViewById(R.id.slidingmenu_market).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), MarketMainActivity.class));
            }
        });
        menu.findViewById(R.id.slidingmenu_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), NoticeActivity.class));
            }
        });
        ApplicationUtil.getInstance().setTypeFace(menu);
    }

    protected Activity getActivity() {
        return this;
    }

    protected Context getContext() {
        return MenuBaseActivity.this;
    }

    protected void openSlidingMenu() {
        menu.showMenu();
    }

    protected void toggleSlidingMenu() {
        menu.toggle();
    }

    private void startNewActivity(Intent intent) {
        finish();
        startActivity(intent);
    }
}
