package com.yscn.knucommunity.Activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.CommentListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class FaqDetailActivity extends BaseBoardDetailActivity implements View.OnClickListener {
    private boolean isReplyMode = false;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BOARD_EDIT_MODE && resultCode == RESULT_OK) {
            setContent();
        }
    }

    private void removeAllReplyData() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.fatdetail_main_scroll_activity);
        int childCount = linearLayout.getChildCount();
        if (childCount > 1) {
            Log.d(getClass().getSimpleName(), "removeAllReplyData");
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
                ImageLoaderUtil.getInstance().initImageLoader();
                String content = value.get("content").toString();
                String title = value.get("title").toString();
                JSONArray fileArray = (JSONArray) value.get("file");

                ((TextView) findViewById(R.id.faq_detail_content)).setText(content);
                ((TextView) findViewById(R.id.faq_detail_title)).setText(title);

                LinearLayout dataView =
                        (LinearLayout) findViewById(R.id.faq_detail_photo_content_view);
                dataView.removeAllViews();

                for (Object obj : fileArray) {
                    ImageView imageView = new ImageView(getContext());
                    int viewLRPadding = (int) ApplicationUtil.getInstance().dpToPx(22);
                    int viewBPadding = (int) ApplicationUtil.getInstance().dpToPx(14);
                    imageView.setPadding(viewLRPadding, 0, viewLRPadding, viewBPadding);
                    dataView.addView(imageView);

                    ImageLoader.getInstance().displayImage(UrlList.BOARD_PHOTO_IMAGE_URL + obj.toString(),
                            imageView, ImageLoaderUtil.getInstance().getDefaultOptions());
                }
                clearProgressDialog.cancel();
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
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.FAQ;
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
