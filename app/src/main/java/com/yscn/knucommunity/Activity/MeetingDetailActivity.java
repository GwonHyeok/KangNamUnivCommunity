package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */

public class MeetingDetailActivity extends BaseBoardDetailActivity implements View.OnClickListener {
    private boolean isReplyMode = false;

    /*
     * FAQ 와 디자인이 같아서 faqdetail 레이아웃이용
     * 나중에 변경 필요
     */
    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_faqdetail);
        super.onCreate(bundle);
        viewInit();
        setContent();
    }

    private void viewInit() {
        findViewById(R.id.view).setOnClickListener(this);
        findViewById(R.id.reply_textview).setOnClickListener(this);

        // FAQ 액티비티를 이용해서 사용하지 않는 뷰 GONE
        findViewById(R.id.faq_detail_replycount).setVisibility(View.GONE);

        // 제목 뷰를 MATCH_PARENT 로 변경
        TextView titleView = (TextView) findViewById(R.id.faq_detail_title);
        ViewGroup.LayoutParams layoutParams = titleView.getLayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        titleView.setLayoutParams(layoutParams);
        titleView.setPadding(0, 0, 0, 0);
        titleView.setGravity(Gravity.CENTER);
    }

    private void setContent() {
        new AsyncTask<Void, Void, String>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
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
            protected void onPostExecute(String value) {
                clearProgressDialog.cancel();
                ((TextView) findViewById(R.id.faq_detail_content)).setText(value);
            }
        }.execute();
    }

    protected void setDefaultData() {
        Intent intent = getIntent();
        int peopleCount = intent.getIntExtra("people", -1);
        String school = intent.getStringExtra("school");
        String major = intent.getStringExtra("major");
        String gender = intent.getStringExtra("gender");
        String time = intent.getStringExtra("time");
        String studentname = intent.getStringExtra("studentname");
        String studentNumber = getIntent().getStringExtra("writerStudentNumber");

        ((TextView) findViewById(R.id.faq_detail_name)).setText(studentname);
        ((TextView) findViewById(R.id.faq_detail_time)).setText(getSimpleDetailTime(time));
        ((TextView) findViewById(R.id.faq_detail_title)).setText(getDefaultMeetingTitle(gender, peopleCount, school, major));

        ImageView profileImageView = (ImageView) findViewById(R.id.faq_detail_profile);
        setProfileImage(profileImageView, studentNumber);
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_main_color);
    }

    private String getDefaultMeetingTitle(String gender, int peopleCount, String school, String major) {
        String title = "";
        if (gender.equals("male")) {
            title += getString(R.string.gender_male);
        } else {
            title += getString(R.string.gender_female);
        }
        title += " | ";
        title += String.format(getString(R.string.community_meeting_people_count), peopleCount);
        title += " | ";
        title += school;
        title += " | ";
        title += major;
        return title;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view) {
            // 자유개시판이랑 같아서 우선 이용
            TextView titleView = ((TextView) findViewById(R.id.faq_detail_title));
            Intent intent = new Intent(this, FreeBoardReplyActivity.class);
            intent.putExtra("contentID", getIntent().getStringExtra("contentID"));
            intent.putExtra("title", titleView.getText().toString());
            startActivity(intent);
        }
    }
}
