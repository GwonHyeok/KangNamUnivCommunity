package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
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
import java.util.HashMap;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class GreenLightDetailActivity extends BaseBoardDetailActivity implements View.OnClickListener {
    private String m_ContentID;

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_greenlightdetail);
        super.onCreate(bundle);
        greenLightButtonInit();
        setContent();
    }

    private void setContent() {
        new AsyncTask<Void, Void, Void>() {
            private ClearProgressDialog clearProgressDialog;
            private HashMap<String, String> greenLightResult;
            private JSONObject jsonObject;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    jsonObject = NetworkUtil.getInstance().getDefaultboardContent(m_ContentID);
                    greenLightResult = NetworkUtil.getInstance().getGreenLightResult(m_ContentID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void value) {
                if (jsonObject != null && greenLightResult != null) {
                    ImageLoaderUtil.getInstance().initImageLoader();
                    JSONArray fileArray = (JSONArray) jsonObject.get("file");

                    ((TextView) findViewById(R.id.greenlight_detail_content)).setText(jsonObject.get("content").toString());
                    ((TextView) findViewById(R.id.greenlight_detail_title)).setText(jsonObject.get("title").toString());

                    String isChecked = greenLightResult.get("isChecked");

                    LinearLayout dataView =
                            (LinearLayout) findViewById(R.id.greenlight_detail_photo_content_view);
                    dataView.removeAllViews();

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

                    if (isChecked.equals("checked")) {
                        Log.d(getClass().getSimpleName(), "Checked GreenLight");
                        String positiveSize = greenLightResult.get("positivesize");
                        String negativeSize = greenLightResult.get("negativesize");
                        setGreenLightOn(positiveSize, negativeSize);
                    }

                } else {
                    ((TextView) findViewById(R.id.greenlight_detail_content))
                            .setText(getContext().getString(R.string.community_board_nodata));
                }
                clearProgressDialog.cancel();
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int resultCode, int requestCode, Intent data) {
        if (resultCode == BOARD_EDIT_MODE && requestCode == RESULT_OK) {
            setContent();
        }
    }

    private void setGreenLightOn(String positiveSize, String negativeSize) {
        TextView lightOn = (TextView) findViewById(R.id.greenlight_light_on);
        TextView lightOff = (TextView) findViewById(R.id.greenlight_light_off);
        lightOn.setBackgroundResource(R.drawable.ic_light_on_pressed);
        lightOff.setBackgroundResource(R.drawable.ic_light_off_pressed);

        lightOn.setText(String.valueOf(positiveSize));
        lightOff.setText(String.valueOf(negativeSize));
        lightOff.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        lightOn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
    }

    private void greenLightButtonInit() {
        /* 그린라이트 버튼 */
        findViewById(R.id.greenlight_light_on).setOnClickListener(this);
        findViewById(R.id.greenlight_light_off).setOnClickListener(this);
        findViewById(R.id.view).setOnClickListener(this);
    }

    @Override
    protected void setDefaultData() {
//        intent.putExtra("contentID", listItems.getContentid());
//        intent.putExtra("writerName", listItems.getName());
//        intent.putExtra("writerStudentNumber", listItems.getStudentnumber());
//        intent.putExtra("title", listItems.getTitle());
//        intent.putExtra("time", listItems.getTime());
        m_ContentID = getIntent().getStringExtra("contentID");
        ((TextView) findViewById(R.id.greenlight_detail_title)).setText(getIntent().getStringExtra("title"));
        ((TextView) findViewById(R.id.greenlight_detail_time)).setText(getSimpleDetailTime(getIntent().getStringExtra("time")));
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.GREENLIGHT;
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_pirmary_dark_color);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.greenlight_light_on) {
            clickGreenRightButton(true);
        } else if (id == R.id.greenlight_light_off) {
            clickGreenRightButton(false);
        } else if (id == R.id.view) {
            Intent intent = new Intent(this, FreeBoardReplyActivity.class);
            intent.putExtra("contentID", getIntent().getStringExtra("contentID"));
            intent.putExtra("title", getIntent().getStringExtra("title"));
            startActivity(intent);
        }
    }

    private void clickGreenRightButton(final boolean isOn) {
        new AsyncTask<Void, Void, HashMap<String, String>>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected HashMap<String, String> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().setGreenLightResult(m_ContentID, isOn);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> value) {
                if (value != null) {
                    String positiveSize = value.get("positivesize");
                    String negativeSize = value.get("negativesize");
                    setGreenLightOn(positiveSize, negativeSize);
                }
                clearProgressDialog.cancel();
            }
        }.execute();
    }
}
