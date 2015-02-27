package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
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
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
        setParallaxScroll();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setContent() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ClearProgressDialog(FreeBoardDetailActivity.this);
                progressDialog.show();
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
            protected void onPostExecute(JSONObject object) {
                progressDialog.cancel();
                if (object == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = object.get("result").toString();
                if (result.equals("fail")) {
                    String reason = object.get("reason").toString();
                    if (reason.equals("notexist")) {
                        AlertToast.error(getContext(), R.string.error_notexist_board_content);
                    } else if (reason.equals("dataerror")) {
                        AlertToast.error(getContext(), getString(R.string.error_to_work));
                    }
                    return;
                }

                /* 에러 처리 필요함 NULL 일 경우 */
                ImageLoaderUtil.getInstance().initImageLoader();
                String content = object.get("content").toString();
                String title = object.get("title").toString();
                String writerName = object.get("writername").toString();
                String writerStudentNumber = object.get("studentnumber").toString();
                String time = object.get("time").toString();

                JSONArray fileArray = (JSONArray) object.get("file");

                ((TextView) findViewById(R.id.freeboard_detail_content)).setText(content);
                ((TextView) findViewById(R.id.freeboard_detail_title)).setText(title);
                ((TextView) findViewById(R.id.freeboard_detail_name)).setText(writerName);
                ((TextView) findViewById(R.id.freeboard_detail_time)).setText(getSimpleDetailTime(time));
                ImageView profileImageView = (ImageView) findViewById(R.id.freeboard_detail_profile);
                setProfileImage(profileImageView, writerStudentNumber);

                findViewById(R.id.linearLayout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        int profileHeight = findViewById(R.id.linearLayout).getHeight();
                        findViewById(R.id.freeboard_detail_scrollview).setPadding(0, profileHeight, 0, 0);
                        findViewById(R.id.linearLayout).removeOnLayoutChangeListener(this);
                    }
                });

                /* 자신이 쓴 글일 경우 메뉴 보여줌 */
                board_studenuNumber = writerStudentNumber;
                invalidateOptionsMenu();

                /* Reset Already Added Photo View */
                LinearLayout dataView =
                        (LinearLayout) findViewById(R.id.freeboard_detail_content_dataview);
                dataView.removeViews(1, dataView.getChildCount() - 1);


                String[] tmpurls = new String[fileArray.size()];
                for (int i = 0; i < fileArray.size(); i++) {
                    Object obj = fileArray.get(i);
                    tmpurls[i] = UrlList.MAIN_URL + obj.toString();
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
                                    FreeBoardDetailActivity.this, v, "imagecollection_transition");
                            ActivityCompat.startActivity(FreeBoardDetailActivity.this, intent, options.toBundle());
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
                progressDialog.cancel();
            }
        }.execute();
    }

    private void setParallaxScroll() {
        ((NotifiableScrollView) findViewById(R.id.freeboard_detail_scrollview)).setonScrollToBottomListener(new NotifiableScrollView.onScrollListener() {
            @Override
            public void scrollToBottom() {

            }

            @Override
            public void onScroll(ScrollView view, int l, int t, int oldl, int oldt) {
                NotifiableScrollView scrollView = (NotifiableScrollView) findViewById(R.id.freeboard_detail_scrollview);
                LinearLayout profileRootView = (LinearLayout) findViewById(R.id.linearLayout);
                View titleView = findViewById(R.id.freeboard_detail_title);
                View profileImageMainView = findViewById(R.id.freeboard_detail_profile_main);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BOARD_EDIT_MODE && resultCode == RESULT_OK) {
            setContent();
        }
    }

    protected void setDefaultData() {
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.FREE;
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_pirmary_dark_color);
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