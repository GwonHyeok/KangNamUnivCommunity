package com.yscn.knucommunity.Activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.CommentListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
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
public class GreenLightReplyActivity extends ActionBarActivity implements View.OnClickListener {
    private boolean isReplyMode = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_freeboardreply);
        actionBarInit();
        findViewById(R.id.freeboard_reply_mainview).setOnClickListener(this);

        /* set Title Data*/
        ((TextView) findViewById(R.id.freeboard_reply_title)).setText(getIntent().getStringExtra("title"));

        findViewById(R.id.reply_textview).setOnClickListener(this);
        getCommentData();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void getCommentData() {
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
                    addCommentData(itemses);
                } else {
                    /* Error Occured */
                }
                clearProgressDialog.cancel();
            }
        }.execute();
    }

    private void addCommentData(ArrayList<CommentListItems> itemses) {
        LinearLayout mainView = (LinearLayout) findViewById(R.id.freeboard_reply_scrollview);
        for (CommentListItems dataObject : itemses) {
            View view = LayoutInflater.from(this).inflate(R.layout.ui_freeboardreply, mainView, false);
            TextView nameTextView = (TextView) view.findViewById(R.id.freeboard_reply_name);
            TextView commentTextView = (TextView) view.findViewById(R.id.freeboard_reply_comment);
            TextView timeTextView = (TextView) view.findViewById(R.id.freeboard_reply_time);
            ImageView profileImageView = (ImageView) view.findViewById(R.id.freeboard_reply_profile);

            /* Set Profile Image */
            ImageLoaderUtil.getInstance().initImageLoader();
            ImageLoader.getInstance().displayImage(
                    UrlList.PROFILE_THUMB_IMAGE_URL + dataObject.getStudentnumber(),
                    profileImageView,
                    ImageLoaderUtil.getInstance().getDefaultOptions());

            /* Fill Comment Data */
            nameTextView.setText(dataObject.getName());
            commentTextView.setText(dataObject.getComment());
            timeTextView.setText(getSimpleTime(dataObject.getTime()));
            mainView.addView(view);
            ApplicationUtil.getInstance().setTypeFace(view);
        }
    }

    private void removeScrollViewData() {
        LinearLayout mainView = (LinearLayout) findViewById(R.id.freeboard_reply_scrollview);
        mainView.removeAllViews();
    }

    private String getSimpleTime(String defaulttime) {
        String dataTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String newDateTimeFormat = "yyyy.MM.dd HH:mm";
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

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.board_white_pirmary_dark_color));
        }

         /* 액션바 */
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
        getMenuInflater().inflate(R.menu.board_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.freeboard_reply_mainview) {
            final View replyImageView = findViewById(R.id.imageView);
            final EditText replyEditText = (EditText) findViewById(R.id.freeboard_reply_edittext);
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
            onClick(findViewById(R.id.freeboard_reply_mainview));
        }
    }

    private void addComment() {
        final String comment = ((EditText) findViewById(R.id.freeboard_reply_edittext)).getText().toString();
        if (comment.isEmpty()) {
            return;
        }
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = false;
                try {
                    result = NetworkUtil.getInstance().checkIsLoginUser().writeComment(
                            getIntent().getStringExtra("contentID"), comment);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    ((EditText) findViewById(R.id.freeboard_reply_edittext)).setText("");
                    removeScrollViewData();
                    getCommentData();
                }
            }
        }.execute();
    }

    private Context getContext() {
        return GreenLightReplyActivity.this;
    }
}
