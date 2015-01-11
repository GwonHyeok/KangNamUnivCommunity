package com.yscn.knucommunity.CustomView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 1. 11..
 */
public abstract class BaseBoardDetailActivity extends ActionBarActivity {
    private String board_studenuNumber;

    /**
     * 반드시 자식 액티비티에서
     * setContentView를 먼저 호출 한 후 super 클래스를 호출해야한다.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStatusBarColor();
        actionBarInit();
        setDefaultData();
        board_studenuNumber = getIntent().getStringExtra("writerStudentNumber");
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getStatusBarColor());
        }
    }

    protected void actionBarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reply_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 자신이 적은 글이 아니면 지우기 메뉴를 가린다.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!board_studenuNumber.equals(UserData.getInstance().getStudentNumber())) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    /**
     * set Default Data Must call
     */
    protected abstract void setDefaultData();

    /**
     * @param defaulttime default database time
     * @return App Board Detail Time
     */
    protected String getSimpleDetailTime(String defaulttime) {
        String dataTimeFormat = "yyyy-MM-dd hh:mm:ss";
        String newDateTimeFormat = "yyyy.MM.dd hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataTimeFormat);
        SimpleDateFormat newDateFormat = new SimpleDateFormat(newDateTimeFormat);

        String time;
        try {
            Date date = simpleDateFormat.parse(defaulttime);
            time = newDateFormat.format(date);
        } catch (java.text.ParseException ignore) {
            time = defaulttime;
        }
        return time;
    }

    protected abstract int getStatusBarColor();

    /**
     * @return activity context
     */
    protected Context getContext() {
        return this;
    }

    /**
     * @param imageView     ProfileImageView
     * @param studentNumber StudentNumber
     */
    protected void setProfileImage(ImageView imageView, String studentNumber) {
        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(UrlList.PROFILE_IMAGE_URL + studentNumber,
                imageView, ImageLoaderUtil.getInstance().getDefaultOptions());
    }
}
