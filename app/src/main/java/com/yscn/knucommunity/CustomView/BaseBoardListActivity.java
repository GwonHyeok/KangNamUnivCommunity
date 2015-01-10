package com.yscn.knucommunity.CustomView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.Items.DefaultBoardListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 1. 10..
 */
public abstract class BaseBoardListActivity extends MenuBaseActivity {
    private int pageIndex = 1;

    /**
     * 반드시 자식 액티비티에서
     * setContentView를 먼저 호출 한 후 super 클래스를 호출해야한다.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStatusBarColor();
        actionBarInit();
        getBoardListData();
    }

    private void actionBarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(getActionBarTitle());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_nav_menu_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
    }

    private void getBoardListData() {
        new AsyncTask<Void, Void, ArrayList<DefaultBoardListItems>>() {
            private ClearProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ClearProgressDialog(getContext());
                progressDialog.show();
            }

            @Override
            protected ArrayList<DefaultBoardListItems> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getDefaultboardList(getBoardType(), getPageIndex());
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<DefaultBoardListItems> listItemses) {
                if (listItemses != null) {
                    addScrollViewData(listItemses);
                }
                progressDialog.cancel();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @param profileImageView BoardList profileImageView
     * @param studentNumber    addScrollViewData(ArrayList<DefaultBoardListItems> listItemses)
     *                         의 어레이에서 학번 정보를 받아와서 넘긴다.
     */
    protected void setProfileImage(ImageView profileImageView, String studentNumber) {
        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(
                UrlList.PROFILE_IMAGE_URL + studentNumber, profileImageView,
                ImageLoaderUtil.getInstance().getDefaultOptions());
    }

    /**
     * @param pageIndex 보드 정보를 가져올떄 인덱스 값.
     */
    protected void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * @return get page Index
     */
    protected int getPageIndex() {
        return this.pageIndex;
    }

    /**
     * @return ActionBarTitle Message
     */
    protected abstract String getActionBarTitle();

    /**
     * @return StatusBar Color
     */
    protected abstract int getStatusBarColor();


    /**
     * @return get BoardType
     */
    protected abstract NetworkUtil.BoardType getBoardType();

    /**
     * 각 레이아웃들의 갑들을 넣어줌
     * 제목, 내용, 시간, 이름, 프로필 이미지 등...
     *
     * @param listItemses is getBoardListData List Data
     */
    protected abstract void addScrollViewData(ArrayList<DefaultBoardListItems> listItemses);

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getStatusBarColor());
        }
    }

    /**
     * @param deftime db상의 기본 DateTime 폼
     * @return 기본 보드에서 사용하는 시간폼으로 변경하여 리턴
     */
    protected String getSimpleListTime(String deftime) {
        String dataTimeFormat = "yyyy-MM-dd hh:mm:ss";
        String newDateTimeFormat = "yyyy.MM.dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataTimeFormat);
        SimpleDateFormat newDateFormat = new SimpleDateFormat(newDateTimeFormat);

        String time;
        try {
            Date date = simpleDateFormat.parse(deftime);
            time = newDateFormat.format(date);
        } catch (java.text.ParseException ignore) {
            // Date Parse Exception
            time = deftime;
        }
        return time;
    }
}