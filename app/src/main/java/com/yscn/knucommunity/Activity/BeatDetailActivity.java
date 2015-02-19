package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Ui.BeatViewPagetAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 15. 2. 19..
 */
public class BeatDetailActivity extends ActionBarActivity {
    private int mBeatIndex;
    private String mContentId;
    private String studentnumber;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_beat_detail);
        toolbarInit();
        setBeatIntentData();
        setBeatContentData();
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

    private void deleteDetailContent() {
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
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("beatid", String.valueOf(mBeatIndex + 1));
                parameter.put("contentid", mContentId);
                try {
                    return NetworkUtil.getInstance().deleteBeatDetail(parameter);
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
                if (result.equals("success")) {
                    AlertToast.success(getApplicationContext(), R.string.success_delete_board);
                    finish();
                }

            }
        }.execute();
    }

    protected void showDeleteDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.warning_title))
                .setMessage(getString(R.string.want_you_delete))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDetailContent();
                    }
                })
                .setNegativeButton(getString(R.string.NO), null)
                .show();
        ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
    }

    private void setBeatContentData() {
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
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("beatid", String.valueOf(mBeatIndex + 1));
                parameter.put("contentid", mContentId);
                try {
                    return NetworkUtil.getInstance().getBeatDetail(parameter);
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
                JSONArray attatchmentArray = (JSONArray) jsonObject.get("attachment");

                String[] attatchmenturls = new String[attatchmentArray.size()];
                for (int i = 0; i < attatchmentArray.size(); i++) {
                    JSONObject attatchmentObject = (JSONObject) attatchmentArray.get(i);
                    attatchmenturls[i] = attatchmentObject.get("filename").toString();
                }
                addPhotoView(attatchmenturls);

                studentnumber = jsonObject.get("studentnumber") == null ? "-1" : jsonObject.get("studentnumber").toString();
                try {
                    ((TextView) findViewById(R.id.beat_detail_title)).setText(jsonObject.get("title").toString());
                    ((TextView) findViewById(R.id.beat_detail_content)).setText(jsonObject.get("content").toString());
                } catch (Exception e) {

                }
                invalidateOptionsMenu();
            }
        }.execute();
    }

    private void addPhotoView(String[] urls) {
        ImageLoaderUtil.getInstance().initImageLoader();
        LinearLayout imageGroup = (LinearLayout) findViewById(R.id.beat_detail_scrollview_imagegroup);

        for (String url : urls) {
            View view = getLayoutInflater().inflate(R.layout.ui_board_image_card, imageGroup, false);
            final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            ImageLoader.getInstance().displayImage(UrlList.MAIN_URL + url, imageView,
                    ImageLoaderUtil.getInstance().getNoCacheImageOptions(), new ImageLoadingListener() {
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

    private void setBeatIntentData() {
        Intent intent = getIntent();
        mBeatIndex = intent.getIntExtra("beatindex", -1);
        mContentId = intent.getStringExtra("contentid");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (UserData.getInstance().getStudentNumber().equals(studentnumber)) {
            getMenuInflater().inflate(R.menu.board_detail_menu, menu);
            menu.getItem(1).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_trash) {
            if (mBeatIndex == BeatViewPagetAdapter.BEAT.QNA.getIndex() ||
                    mBeatIndex == BeatViewPagetAdapter.BEAT.REVIEW.getIndex()) {
                showDeleteDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}