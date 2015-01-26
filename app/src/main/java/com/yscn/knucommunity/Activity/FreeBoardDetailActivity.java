package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
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
                /* 에러 처리 필요함 NULL 일 경우 */
                ImageLoaderUtil.getInstance().initImageLoader();
                String content = object.get("content").toString();
                String title = object.get("title").toString();

                JSONArray fileArray = (JSONArray) object.get("file");

                ((TextView) findViewById(R.id.freeboard_detail_content)).setText(content);
                ((TextView) findViewById(R.id.freeboard_detail_title)).setText(title);

                /* Reset Already Added Photo View */
                LinearLayout dataView =
                        (LinearLayout) findViewById(R.id.freeboard_detail_content_dataview);
                dataView.removeViews(1, dataView.getChildCount() - 1);

                for (Object obj : fileArray) {
                    View fileImageView = LayoutInflater.from(getContext()).inflate(R.layout.ui_board_image_card, dataView, false);
                    final ImageView imageView = (ImageView) fileImageView.findViewById(R.id.imageView);
                    final ProgressBar progressBar = (ProgressBar) fileImageView.findViewById(R.id.progressbar);
                    dataView.addView(fileImageView);

                    ImageLoader.getInstance().displayImage(UrlList.BOARD_PHOTO_IMAGE_URL + obj.toString(),
                            imageView, ImageLoaderUtil.getInstance().getNoCacheImageOptions(), new ImageLoadingListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BOARD_EDIT_MODE && resultCode == RESULT_OK) {
            setContent();
        }
    }

    protected void setDefaultData() {
        String writerName = getIntent().getStringExtra("writerName");
        String writerStudentNumber = getIntent().getStringExtra("writerStudentNumber");
        String title = getIntent().getStringExtra("title");
        String time = getIntent().getStringExtra("time");

        ((TextView) findViewById(R.id.freeboard_detail_title)).setText(title);
        ((TextView) findViewById(R.id.freeboard_detail_name)).setText(writerName);
        ((TextView) findViewById(R.id.freeboard_detail_time)).setText(getSimpleDetailTime(time));
        ImageView profileImageView = (ImageView) findViewById(R.id.freeboard_detail_profile);

        setProfileImage(profileImageView, writerStudentNumber);
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.FREE;
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_main_color);
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