package com.yscn.knucommunity.CustomView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.Activity.BoardWriteActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 1. 11..
 */
public abstract class BaseBoardDetailActivity extends ActionBarActivity {
    private String board_studenuNumber, board_contentID;
    protected int BOARD_EDIT_MODE = 0X12;

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
        board_contentID = getIntent().getStringExtra("contentID");
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

    /**
     * 메뉴 인플레이트
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 옵션 메뉴 선택시 글 삭제 혹은 공유 작업.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_trash) {
            deleteBoardContent();
        } else if (item.getItemId() == R.id.action_edit) {
            editBoardContent();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 글 수정, 기본적으로 각각 보드 타입별로 글 적는 액티비티를 이용
     */
    private void editBoardContent() {
        NetworkUtil.BoardType boardType = getBoardType();
        Intent intent;
        if (boardType == NetworkUtil.BoardType.FREE
                || boardType == NetworkUtil.BoardType.FAQ
                || boardType == NetworkUtil.BoardType.GREENLIGHT) {
            intent = new Intent(getContext(), BoardWriteActivity.class);
            intent.putExtra("boardType", boardType.getValue());
            intent.putExtra("isEditMode", true);
            intent.putExtra("contentid", board_contentID);
            startActivityForResult(intent, BOARD_EDIT_MODE);
        }
    }

    /**
     * 글 삭제하는 작업
     */
    private void deleteBoardContent() {
        new AsyncTask<Void, Void, Boolean>() {
            private ClearProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ClearProgressDialog(getContext());
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = false;
                try {
                    result = NetworkUtil.getInstance().deleteBoardList(board_contentID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    finish();
                }
                progressDialog.dismiss();
            }
        }.execute();
    }

    /**
     * 자신이 적은 글이 아니면 지우기 메뉴를 가린다.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!board_studenuNumber.equals(UserData.getInstance().getStudentNumber())) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    /**
     * set Default Data Must call
     */
    protected abstract void setDefaultData();

    /**
     * @return Board Type depend on NetworkUtil.BoardType
     */
    protected abstract NetworkUtil.BoardType getBoardType();

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
        ImageLoader.getInstance().displayImage(UrlList.PROFILE_THUMB_IMAGE_URL + studentNumber,
                imageView, ImageLoaderUtil.getInstance().getDefaultOptions());
    }
}
