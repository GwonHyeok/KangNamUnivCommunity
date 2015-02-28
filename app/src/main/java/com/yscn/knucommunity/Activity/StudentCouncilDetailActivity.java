package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 2. 28..
 */
public class StudentCouncilDetailActivity extends ActionBarActivity {

    private String mContentId;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_studentcouncil_detail);
        toolbarInit();
        setStudentCouncilIntentData();
        setStudentCoincilContentData();
        setParallaxScroll();
        ApplicationUtil.getInstance().setTypeFace(findViewById(R.id.beat_detail_root));
    }

    private void toolbarInit() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_cancel);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setStudentCoincilContentData() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ProgressBar mProgressbar;

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getApplicationContext(), R.string.error_check_network_state);
                    cancel(true);
                    return;
                }
                mProgressbar = (ProgressBar) findViewById(R.id.beat_detail_progressbar);
                mProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getCouncilInfoDetail(mContentId);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                mProgressbar.setVisibility(View.GONE);
                if (jsonObject == null) {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                    return;
                }
                String result = jsonObject.get("result").toString();

                if (result.equals("fail")) {
                    String reason = jsonObject.get("reason").toString();
                    if (reason.equals("nodata")) {
                        AlertToast.error(getApplicationContext(), R.string.error_notexist_board_content);
                    } else {
                        AlertToast.error(getApplicationContext(), R.string.error_to_work);
                    }
                    return;
                }

                Log.d(getClass().getSimpleName(), jsonObject.toJSONString());
                JSONArray attatchmentArray = (JSONArray) jsonObject.get("file");

                String[] attatchmenturls = new String[attatchmentArray.size()];
                for (int i = 0; i < attatchmentArray.size(); i++) {
                    JSONObject attatchmentObject = (JSONObject) attatchmentArray.get(i);
                    attatchmenturls[i] = UrlList.MAIN_URL + attatchmentObject.get("filename").toString();
                }
                addPhotoView(attatchmenturls);


                ImageLoaderUtil.getInstance().initImageLoader();
//                ImageView profileView = (ImageView) findViewById(R.id.beat_detail_profile);
//                TextView nameView = (TextView) findViewById(R.id.beat_detail_name);

//                profileView.setImageResource(R.drawable.ic_beat_profile);
//                nameView.setText(R.string.profile_name_beat);


                findViewById(R.id.linearLayout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        int profileHeight = findViewById(R.id.linearLayout).getHeight();
                        findViewById(R.id.beat_detail_scrollview).setPadding(0, profileHeight, 0, 0);
                        findViewById(R.id.linearLayout).removeOnLayoutChangeListener(this);
                    }
                });

                try {
                    ((TextView) findViewById(R.id.beat_detail_title)).setText(jsonObject.get("title").toString());
                    ((TextView) findViewById(R.id.beat_detail_content)).setText(jsonObject.get("content").toString());
                    ((TextView) findViewById(R.id.beat_detail_time)).setText(getSimpleDetailTime(jsonObject.get("time").toString()));
                } catch (Exception e) {

                }
                invalidateOptionsMenu();
            }
        }.execute();
    }

    private void addPhotoView(final String[] urls) {
        ImageLoaderUtil.getInstance().initImageLoader();
        LinearLayout imageGroup = (LinearLayout) findViewById(R.id.beat_detail_scrollview_imagegroup);

        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            final int imagePosition = i;

            View view = getLayoutInflater().inflate(R.layout.ui_board_image_card, imageGroup, false);
            /* Start Preview Activity */
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImageCollectionActivity.class);
                    intent.putExtra("Imageurls", urls);
                    intent.putExtra("Position", imagePosition);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            StudentCouncilDetailActivity.this, v, "imagecollection_transition");
                    ActivityCompat.startActivity(StudentCouncilDetailActivity.this, intent, options.toBundle());
                }
            });

            final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            ImageLoader.getInstance().displayImage(url, imageView,
                    ImageLoaderUtil.getInstance().getDiskCacheImageOptions(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            progressBar.setVisibility(View.VISIBLE);
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
            imageGroup.addView(view);
        }
    }

    private void setStudentCouncilIntentData() {
        Intent intent = getIntent();
        mContentId = intent.getStringExtra("contentid");
    }

    private void setParallaxScroll() {
        ((NotifiableScrollView) findViewById(R.id.beat_detail_scrollview)).setonScrollToBottomListener(new NotifiableScrollView.onScrollListener() {
            @Override
            public void scrollToBottom() {

            }

            @Override
            public void onScroll(ScrollView view, int l, int t, int oldl, int oldt) {
                NotifiableScrollView scrollView = (NotifiableScrollView) findViewById(R.id.beat_detail_scrollview);
                LinearLayout profileRootView = (LinearLayout) findViewById(R.id.linearLayout);
                View titleView = findViewById(R.id.beat_detail_title);
                View profileImageMainView = findViewById(R.id.beat_detail_profile_main);

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

    protected String getSimpleDetailTime(String defaulttime) {
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
}
