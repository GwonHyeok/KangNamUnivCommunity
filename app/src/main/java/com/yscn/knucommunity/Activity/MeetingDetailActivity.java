package com.yscn.knucommunity.Activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.NotifiableScrollView;
import com.yscn.knucommunity.Items.CommentListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UserData;

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
        setParallaxScroll();
    }

    private void setParallaxScroll() {
        ((NotifiableScrollView) findViewById(R.id.faq_detail_scrollview)).setonScrollToBottomListener(new NotifiableScrollView.onScrollListener() {
            @Override
            public void scrollToBottom() {

            }

            @Override
            public void onScroll(ScrollView view, int l, int t, int oldl, int oldt) {
                NotifiableScrollView scrollView = (NotifiableScrollView) findViewById(R.id.faq_detail_scrollview);
                LinearLayout profileRootView = (LinearLayout) findViewById(R.id.linearLayout);
                View titleView = findViewById(R.id.faq_detail_title_group);
                View profileImageMainView = findViewById(R.id.faq_detail_infoview);

                int profileMainHeight = findViewById(R.id.linearLayout).getHeight();
                int scrollviewContentsHeight = scrollView.getChildAt(0).getHeight();
                int scrollviewHeight = scrollView.getHeight();


                /* 만약 컨텐츠 크기가 스크롤뷰 크기 보다 작으면 스크롤 X */
                if (scrollviewContentsHeight < scrollviewHeight) {
                    return;
                }

                // toolbarHeight : t  = 100 : x
                // 100t = toolbarHeightx
                // 1.0 * t / toolbarHeight = 투명도

                float profileImageMainViewY = profileImageMainView.getY();
                boolean isAnimate = profileImageMainViewY > t;
                float alpha = isAnimate ? 1.0f - ((1.0f * t / profileImageMainViewY) * 1.5f) : 0f;
                float translationY = isAnimate ? -t : -profileImageMainViewY;
                int padding = isAnimate ? profileMainHeight - t : (int) (profileMainHeight - profileImageMainViewY);

                titleView.setAlpha(alpha);
                profileRootView.setTranslationY(translationY);
                scrollView.setPadding(0, padding, 0, 0);
            }
        });
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

        for (final CommentListItems dataObject : itemses) {
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

            if (dataObject.getStudentnumber().equals(UserData.getInstance().getStudentNumber())) {
                View view = replyView.findViewById(R.id.faq_reply_delete_comment);
                view.setVisibility(View.VISIBLE);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteCommentDialog(dataObject.getCommentid());
                    }
                });
            } else {
                timeView.setPadding(0, 0, (int) ApplicationUtil.getInstance().dpToPx(8), 0);
            }
        }
    }

    private void showDeleteCommentDialog(final String commentid) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.warning_title)
                .setMessage(R.string.want_you_delete_comment)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteComment(commentid);
                    }
                })
                .setNegativeButton(R.string.NO, null)
                .show();
    }

    private void deleteComment(final String commentid) {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().deleteComment(commentid);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject itemses) {
                clearProgressDialog.cancel();
                if (itemses == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = itemses.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), getString(R.string.success_board_comment_delete));
                    removeAllReplyData();
                    addComment();
                } else if (result.equals("fail")) {
                    /* 토큰이나 데이터, 혹은 자기글이 아님 */
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                }
            }
        }.execute();
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
        new AsyncTask<Void, Void, Void>() {
            private ClearProgressDialog clearProgressDialog;
            JSONObject meetingDataObject;
            JSONObject boardContentObject;
            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String contentID = getIntent().getStringExtra("contentID");
                try {
                    meetingDataObject = NetworkUtil.getInstance().getMeetingData(contentID);
                    boardContentObject = NetworkUtil.getInstance().getDefaultboardContent(contentID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void value) {
                clearProgressDialog.cancel();
                if (boardContentObject != null && meetingDataObject != null) {

                    String peopleCount = meetingDataObject.get("studentcount").toString();
                    String school = meetingDataObject.get("schoolname").toString();
                    String major = meetingDataObject.get("majorname").toString();
                    String gender = meetingDataObject.get("gender").toString();
                    String time = boardContentObject.get("time").toString();
                    String studentname = boardContentObject.get("writername").toString();
                    String studentNumber = boardContentObject.get("studentnumber").toString();
                    board_studenuNumber = studentNumber;
                    invalidateOptionsMenu();

                    ((TextView) findViewById(R.id.faq_detail_name)).setText(studentname);
                    ((TextView) findViewById(R.id.faq_detail_time)).setText(getSimpleDetailTime(time));
                    ((TextView) findViewById(R.id.faq_detail_title)).setText(getDefaultMeetingTitle(gender, Integer.parseInt(peopleCount), school, major));

                    ImageView profileImageView = (ImageView) findViewById(R.id.faq_detail_profile);
                    setProfileImage(profileImageView, studentNumber);
                    ((TextView) findViewById(R.id.faq_detail_content)).setText(boardContentObject.get("content").toString());
                }
                findViewById(R.id.linearLayout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        int profileHeight = findViewById(R.id.linearLayout).getHeight();
                        findViewById(R.id.faq_detail_scrollview).setPadding(0, profileHeight, 0, 0);
                        findViewById(R.id.linearLayout).removeOnLayoutChangeListener(this);
                    }
                });
            }
        }.execute();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        /* 미팅은 무조건 글을 수정 할 수 없다. */
        menu.getItem(1).setVisible(false);
        return true;
    }

    protected void setDefaultData() {
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.MEETING;
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
                    ((EditText) findViewById(R.id.reply_edittext)).setText("");
                    removeAllReplyData();
                    getReplyData();
                }
            }
        }.execute();
    }
}
