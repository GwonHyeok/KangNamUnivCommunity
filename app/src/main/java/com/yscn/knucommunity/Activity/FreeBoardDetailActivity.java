package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FreeBoardDetailActivity extends BaseBoardDetailActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_freeboarddetail);
        super.onCreate(bundle);
        findViewById(R.id.freeboard_replayview).setOnClickListener(this);
        setContent();
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
                    return NetworkUtil.getInstance().getDefaultboardContent(conetntID);
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

    protected void setDefaultData() {
        String writerName = getIntent().getStringExtra("writerName");
        String writerStudentNumber = getIntent().getStringExtra("writerStudentNumber");
        String title = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");

        ((TextView) findViewById(R.id.freeboard_detail_title)).setText(title);
        ((TextView) findViewById(R.id.freeboard_detail_name)).setText(writerName);
        ((TextView) findViewById(R.id.freeboard_detail_time)).setText(getSimpleDetailTime(time));
        ImageView profileImageView = (ImageView) findViewById(R.id.freeboard_detail_profile);

        setProfileImage(profileImageView, writerStudentNumber);
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_main_color);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.freeboard_replayview) {
            Intent intent = new Intent(this, FreeBoardReplyActivity.class);
            intent.putExtra("contentID", getIntent().getStringExtra("contentID"));
            intent.putExtra("title", getIntent().getStringExtra("title"));
            startActivity(intent);
        }
    }
}