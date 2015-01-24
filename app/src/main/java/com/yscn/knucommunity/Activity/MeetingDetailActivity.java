package com.yscn.knucommunity.Activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.CommentListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

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
        getReplyData();
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

    private void getReplyData() {
        new AsyncTask<Void, Void, ArrayList<CommentListItems>>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected ArrayList<CommentListItems> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getComment(getIntent().getStringExtra("contentID"));
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<CommentListItems> itemses) {
                if (itemses != null) {
                    addReplyData(itemses);
                } else {
                    /* Error Occured */
                }
                clearProgressDialog.cancel();
            }
        }.execute();
    }

    private void addReplyData(ArrayList<CommentListItems> itemses) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.fatdetail_main_scroll_activity);

        for (CommentListItems dataObject : itemses) {
            View replyView = LayoutInflater.from(this).inflate(R.layout.ui_faqreply, linearLayout, false);

            ImageView profileView = (ImageView) replyView.findViewById(R.id.faq_reply_profile);
            TextView nameView = (TextView) replyView.findViewById(R.id.faq_reply_name);
            TextView timeView = (TextView) replyView.findViewById(R.id.faq_reply_time);
            TextView contentView = (TextView) replyView.findViewById(R.id.faq_reply_content);

            nameView.setText(getReplyNameFormat(dataObject.getName()));
            timeView.setText(getSimpleDetailTime(dataObject.getTime()));
            contentView.setText(dataObject.getComment());
            setProfileImage(profileView, dataObject.getStudentnumber());

            linearLayout.addView(replyView);
        }
    }

    private void removeAllReplyData() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.fatdetail_main_scroll_activity);
        int childCount = linearLayout.getChildCount();
        if (childCount > 1) {
            linearLayout.removeViews(1, childCount - 1);
        }
    }

    private String getReplyNameFormat(String replyName) {
        return String.format(getString(R.string.community_faq_reply_who_txt), replyName);
    }

    private void setContent() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                String conetntID = getIntent().getStringExtra("contentID");
                try {
                    return NetworkUtil.getInstance().getDefaultboardContent(conetntID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(JSONObject value) {
                clearProgressDialog.cancel();
                ((TextView) findViewById(R.id.faq_detail_content)).setText(value.get("content").toString());
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

    private String getReplyText(String reply) {
        return String.format(getString(R.string.community_faq_reply_txt), reply);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view) {
            final View replyImageView = findViewById(R.id.imageView);
            final EditText replyEditText = (EditText) findViewById(R.id.reply_edittext);
            View replyButtonView = findViewById(R.id.reply_textview);

            int moveX = (int) (replyImageView.getX() + replyImageView.getWidth());

            replyEditText.setCursorVisible(!isReplyMode);
            replyEditText.setEnabled(!isReplyMode);
            replyEditText.setSelection(replyEditText.length());
            replyButtonView.setVisibility(isReplyMode ? View.GONE : View.VISIBLE);
            replyEditText.setHint(isReplyMode ?
                    getString(R.string.community_reply_text) : getString(R.string.community_reply_need_text));

            ValueAnimator animation = ValueAnimator.ofFloat(0, -moveX);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    replyImageView.setTranslationX((float) animation.getAnimatedValue());
                    replyEditText.setTranslationX((float) animation.getAnimatedValue());
                }
            });

            if (!isReplyMode) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(replyEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 400);
            }

            animation.setDuration(400);
            animation.start();

            isReplyMode = !isReplyMode;

        } else if (id == R.id.reply_textview) {
            addComment();
            onClick(findViewById(R.id.view));
        }
    }

    private void addComment() {
        final String comment = ((EditText) findViewById(R.id.reply_edittext)).getText().toString();

        if (comment.isEmpty()) {
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = false;
                try {
                    result = NetworkUtil.getInstance().writeComment(
                            getIntent().getStringExtra("contentID"), comment);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    ((EditText) findViewById(R.id.reply_edittext)).setText("");
                    removeAllReplyData();
                    getReplyData();
                }
            }
        }.execute();
    }
}
