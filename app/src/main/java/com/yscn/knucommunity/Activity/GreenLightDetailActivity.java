package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setContent() {
        new AsyncTask<Void, Void, Void>() {
            private ClearProgressDialog clearProgressDialog;
            private JSONObject contentObject;
            private JSONObject greenLightObject;

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    clearProgressDialog = new ClearProgressDialog(getContext());
                    clearProgressDialog.show();
                } else {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(true);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    contentObject = NetworkUtil.getInstance().getDefaultboardContent(m_ContentID);
                    greenLightObject = NetworkUtil.getInstance().getGreenLightResult(m_ContentID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void value) {
                clearProgressDialog.cancel();
                if (contentObject != null && greenLightObject != null) {

                    String result = contentObject.get("result").toString();
                    if (result.equals("fail")) {
                        String reason = contentObject.get("reason").toString();
                        if (reason.equals("notexist")) {
                            AlertToast.error(getContext(), R.string.error_notexist_board_content);
                        } else if (reason.equals("dataerror")) {
                            AlertToast.error(getContext(), getString(R.string.error_to_work));
                        }
                        return;
                    }

                    result = greenLightObject.get("result").toString();
                    if (result.equals("fail")) {
                        String reason = greenLightObject.get("reason").toString();
                        if (reason.equals("emptyuserinfo")) {
                            AlertToast.error(getContext(), R.string.error_empty_studentnumber_info);
                            UserData.getInstance().logoutUser();
                        }
                        return;
                    }

                    ImageLoaderUtil.getInstance().initImageLoader();
                    JSONArray fileArray = (JSONArray) contentObject.get("file");

                    ((TextView) findViewById(R.id.greenlight_detail_content)).setText(contentObject.get("content").toString());
                    ((TextView) findViewById(R.id.greenlight_detail_title)).setText(contentObject.get("title").toString());

                    board_studenuNumber = contentObject.get("studentnumber").toString();
                    invalidateOptionsMenu();

                    String isChecked = greenLightObject.get("isChecked").toString();

                    LinearLayout dataView =
                            (LinearLayout) findViewById(R.id.greenlight_detail_photo_content_view);
                    dataView.removeAllViews();

                    String[] tmpurls = new String[fileArray.size()];
                    for (int i = 0; i < tmpurls.length; i++) {
                        tmpurls[i] = UrlList.MAIN_URL_IMAGE + fileArray.get(i).toString();
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
                                        GreenLightDetailActivity.this, v, "imagecollection_transition");
                                ActivityCompat.startActivity(GreenLightDetailActivity.this, intent, options.toBundle());
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

                    if (isChecked.equals("checked")) {
                        Log.d(getClass().getSimpleName(), "Checked GreenLight");
                        String positiveSize = greenLightObject.get("positivesize").toString();
                        String negativeSize = greenLightObject.get("negativesize").toString();
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

        lightOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.warning_title)
                        .setMessage(R.string.text_greenlight_change)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clickGreenRightButton(true);
                            }
                        })
                        .setNegativeButton(R.string.NO, null)
                        .show();
                ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
            }
        });

        lightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.warning_title)
                        .setMessage(R.string.text_greenlight_change)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clickGreenRightButton(false);
                            }
                        })
                        .setNegativeButton(R.string.NO, null)
                        .show();
                ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
            }
        });
    }

    private void greenLightButtonInit() {
        /* 그린라이트 버튼 */
        findViewById(R.id.greenlight_light_on).setOnClickListener(this);
        findViewById(R.id.greenlight_light_off).setOnClickListener(this);
        findViewById(R.id.view).setOnClickListener(this);
    }

    @Override
    protected void setDefaultData() {
        m_ContentID = getIntent().getStringExtra("contentID");
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
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(true);
                    return;
                }
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().setGreenLightResult(m_ContentID, isOn);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject value) {
                clearProgressDialog.cancel();

                if (value == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }

                String result = value.get("result").toString();

                if (result.equals("success")) {
                    String positiveSize = value.get("positivesize").toString();
                    String negativeSize = value.get("negativesize").toString();
                    setGreenLightOn(positiveSize, negativeSize);
                    return;
                }

                if (result.equals("fail")) {
                    String reason = value.get("reason").toString();
                    if (reason.equals("emptyuserinfo")) {
                        AlertToast.error(getContext(), R.string.error_empty_studentnumber_info);
                        UserData.getInstance().logoutUser();
                    }
                }
            }
        }.execute();
    }
}
