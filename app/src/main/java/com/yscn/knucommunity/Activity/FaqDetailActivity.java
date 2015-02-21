package com.yscn.knucommunity.Activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.NotifiableScrollView;
import com.yscn.knucommunity.Items.CommentListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

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
        setParallaxScroll();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
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
        findViewById(R.id.reply_edittext).setOnClickListener(this);
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
                    return NetworkUtil.getInstance().checkIsLoginUser().getComment(getIntent().getStringExtra("contentID"));
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

        if (itemses.size() > 0) {
            findViewById(R.id.linearLayout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int profileHeight = findViewById(R.id.linearLayout).getHeight();
                    findViewById(R.id.faq_detail_scrollview).setPadding(0, profileHeight, 0, 0);
                    findViewById(R.id.linearLayout).removeOnLayoutChangeListener(this);
                }
            });
            ((TextView) findViewById(R.id.faq_detail_replycount)).setText(getReplyText(String.valueOf(itemses.size())));
        }

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
                View deleteView = replyView.findViewById(R.id.faq_reply_delete_comment);
                deleteView.setVisibility(View.VISIBLE);
                deleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteCommentDialog(dataObject.getCommentid());
                    }
                });
            } else {
                timeView.setPadding(0, 0, (int) ApplicationUtil.getInstance().dpToPx(8), 0);
            }

            ApplicationUtil.getInstance().setTypeFace(replyView);
        }
    }

    private void showDeleteCommentDialog(final String commentid) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
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
        ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
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
                String title = getDefaulttFaqTitle(value.get("title").toString());
                String writerName = value.get("writername").toString();
                String studentNumber = value.get("studentnumber").toString();
                String time = getSimpleDetailTime(value.get("time").toString());

                JSONArray fileArray = (JSONArray) value.get("file");

                ((TextView) findViewById(R.id.faq_detail_content)).setText(content);
                ((TextView) findViewById(R.id.faq_detail_title)).setText(title);
                ((TextView) findViewById(R.id.faq_detail_name)).setText(writerName);
                ((TextView) findViewById(R.id.faq_detail_time)).setText(getSimpleDetailTime(time));

                board_studenuNumber = studentNumber;
                invalidateOptionsMenu();

                ImageView profileImageView = (ImageView) findViewById(R.id.faq_detail_profile);
                setProfileImage(profileImageView, studentNumber);

                LinearLayout dataView =
                        (LinearLayout) findViewById(R.id.faq_detail_photo_content_view);
                dataView.removeAllViews();

                String[] tmpurls = new String[fileArray.size()];
                for (int i = 0; i < tmpurls.length; i++) {
                    tmpurls[i] = UrlList.MAIN_URL + fileArray.get(i).toString();
                }

                final String[] urls = tmpurls;
                for (int i = 0; i < urls.length; i++) {
                    final int imagePosition = i;
                    View fileImageView = LayoutInflater.from(getContext()).inflate(R.layout.ui_board_image_card, dataView, false);
                    final ImageView imageView = (ImageView) fileImageView.findViewById(R.id.imageView);
                    final ProgressBar progressBar = (ProgressBar) fileImageView.findViewById(R.id.progressbar);
                    dataView.addView(fileImageView);
                    fileImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ImageCollectionActivity.class);
                            intent.putExtra("Imageurls", urls);
                            intent.putExtra("Position", imagePosition);

                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    FaqDetailActivity.this, v, "imagecollection_transition");
                            ActivityCompat.startActivity(FaqDetailActivity.this, intent, options.toBundle());
                        }
                    });

                    ImageLoader.getInstance().displayImage(urls[i],
                            imageView, ImageLoaderUtil.getInstance().getDiskCacheImageOptions(), new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
                findViewById(R.id.linearLayout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        int profileHeight = findViewById(R.id.linearLayout).getHeight();
                        findViewById(R.id.faq_detail_scrollview).setPadding(0, profileHeight, 0, 0);
                        findViewById(R.id.linearLayout).removeOnLayoutChangeListener(this);
                    }
                });
                clearProgressDialog.cancel();
            }
        }.execute();
    }

    protected void setDefaultData() {
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.FAQ;
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_pirmary_dark_color);
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
        } else if (id == R.id.reply_edittext) {
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
