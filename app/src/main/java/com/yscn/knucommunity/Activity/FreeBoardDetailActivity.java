package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FreeBoardDetailActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_freeboarddetail);
        findViewById(R.id.freeboard_replayview).setOnClickListener(this);
        setDefaultData();
        setContent();
        viewInit();
    }

    private void setContent() {
        new AsyncTask<Void, Void, String>() {
            private ClearProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ClearProgressDialog(FreeBoardDetailActivity.this);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String conetntID = getIntent().getStringExtra("contentID");
                try {
                    return NetworkUtil.getInstance().getFreeboardContent(conetntID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String content) {
                /* 에러 처리 필요함 NULL 일 경우 */
                ((TextView) findViewById(R.id.freeboard_detail_content)).setText(content);
                progressDialog.cancel();
            }
        }.execute();
    }

    private void setDefaultData() {
        String writerName = getIntent().getStringExtra("writerName");
        String writerStudentNumber = getIntent().getStringExtra("writerStudentNumber");
        String title = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");

        ((TextView) findViewById(R.id.freeboard_detail_title)).setText(title);
        ((TextView) findViewById(R.id.freeboard_detail_name)).setText(writerName);
        ((TextView) findViewById(R.id.freeboard_detail_time)).setText(getSimpleTime(time));
        ImageView profileImageView = (ImageView) findViewById(R.id.freeboard_detail_profile);

        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(UrlList.PROFILE_IMAGE_URL + writerStudentNumber,
                profileImageView, ImageLoaderUtil.getInstance().getDefaultOptions());
    }

    private String getSimpleTime(String defaulttime) {
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

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFFFAFAFA);
        }

         /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_community_base, null);
        ((TextView) view.findViewById(R.id.actionbar_community_base_title)).setText("");
        ((ImageView) view.findViewById(R.id.actionbar_community_base_image)).setImageResource(R.drawable.ic_cancel);
        ((ImageView) view.findViewById(R.id.actionbar_community_first_image)).setImageResource(R.drawable.ic_trash);
        ((ImageView) view.findViewById(R.id.actionbar_community_second_image)).setImageResource(R.drawable.ic_share);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAFAFA")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);

        view.findViewById(R.id.actionbar_community_base_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FreeBoardDetailActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.freeboard_replayview) {
            startActivity(new Intent(this, FreeBoardReplyActivity.class));
        }
    }
}
