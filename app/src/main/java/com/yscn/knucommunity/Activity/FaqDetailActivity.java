package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.CommentListItems;
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
 * Created by GwonHyeok on 14. 11. 5..
 */
public class FaqDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_faqdetail);
        viewInit();
        setDefaultData();
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
        ImageLoaderUtil.getInstance().initImageLoader();

        for (CommentListItems dataObject : itemses) {
            View replyView = LayoutInflater.from(this).inflate(R.layout.ui_faqreply, linearLayout, false);

            ImageView profileView = (ImageView) replyView.findViewById(R.id.faq_reply_profile);
            TextView nameView = (TextView) replyView.findViewById(R.id.faq_reply_name);
            TextView timeView = (TextView) replyView.findViewById(R.id.faq_reply_time);
            TextView contentView = (TextView) replyView.findViewById(R.id.faq_reply_content);

            nameView.setText(getReplyNameFormat(dataObject.getName()));
            timeView.setText(getSimpleTime(dataObject.getTime()));
            contentView.setText(dataObject.getComment());

            ImageLoader.getInstance().displayImage(
                    UrlList.PROFILE_IMAGE_URL + dataObject.getStudentnumber(),
                    profileView);

            linearLayout.addView(replyView);
        }
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

    private void setDefaultData() {
        String contentID = getIntent().getStringExtra("contentID");
        String writerName = getIntent().getStringExtra("writerName");
        String studentNumber = getIntent().getStringExtra("writerStudentNumber");
        String title = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");
        String replyCount = String.valueOf(getIntent().getIntExtra("replyCount", -1));

        ((TextView) findViewById(R.id.faq_detail_name)).setText(writerName);
        ((TextView) findViewById(R.id.faq_detail_time)).setText(getSimpleTime(time));
        ((TextView) findViewById(R.id.faq_detail_title)).setText(getDefaulttFaqTitle(title));
        ((TextView) findViewById(R.id.faq_detail_replycount)).setText(getReplyText(replyCount));

        ImageLoaderUtil.getInstance().initImageLoader();
        ImageView profileImageView = (ImageView) findViewById(R.id.faq_detail_profile);
        ImageLoader.getInstance().displayImage(UrlList.PROFILE_IMAGE_URL + studentNumber, profileImageView);
    }

    private String getDefaulttFaqTitle(String title) {
        return String.format(getString(R.string.comminity_faq_question_title), title);
    }

    private String getReplyText(String reply) {
        return String.format(getString(R.string.community_faq_reply_txt), reply);
    }


    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.board_white_main_color));
        }

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

    private Context getContext() {
        return FaqDetailActivity.this;
    }
}
