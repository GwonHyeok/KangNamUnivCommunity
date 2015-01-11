package com.yscn.knucommunity.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.CommentListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class FaqDetailActivity extends BaseBoardDetailActivity {

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_faqdetail);
        super.onCreate(bundle);
        setContent();
        getReplyData();
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

    private String getReplyNameFormat(String replyName) {
        return String.format(getString(R.string.community_faq_reply_who_txt), replyName);
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
        String contentID = getIntent().getStringExtra("contentID");
        String writerName = getIntent().getStringExtra("writerName");
        String studentNumber = getIntent().getStringExtra("writerStudentNumber");
        String title = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");
        String replyCount = String.valueOf(getIntent().getIntExtra("replyCount", -1));

        ((TextView) findViewById(R.id.faq_detail_name)).setText(writerName);
        ((TextView) findViewById(R.id.faq_detail_time)).setText(getSimpleDetailTime(time));
        ((TextView) findViewById(R.id.faq_detail_title)).setText(getDefaulttFaqTitle(title));
        ((TextView) findViewById(R.id.faq_detail_replycount)).setText(getReplyText(replyCount));

        ImageView profileImageView = (ImageView) findViewById(R.id.faq_detail_profile);
        setProfileImage(profileImageView, studentNumber);
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_main_color);
    }

    private String getDefaulttFaqTitle(String title) {
        return String.format(getString(R.string.comminity_faq_question_title), title);
    }

    private String getReplyText(String reply) {
        return String.format(getString(R.string.community_faq_reply_txt), reply);
    }
}
