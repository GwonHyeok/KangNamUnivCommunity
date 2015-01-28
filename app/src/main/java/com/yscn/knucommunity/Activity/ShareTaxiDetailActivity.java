package com.yscn.knucommunity.Activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.CircleImageView;
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
 * Created by GwonHyeok on 15. 1. 26..
 */
public class ShareTaxiDetailActivity extends BaseBoardDetailActivity implements View.OnClickListener {
    private boolean isFolded = true;

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_sharetaxidetail);
        super.onCreate(bundle);

        setTaxiData();
        findViewById(R.id.share_taxi_detail_with_info_root_view).setOnClickListener(this);

        findViewById(R.id.share_taxi_detail_replyview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FreeBoardReplyActivity.class);
                intent.putExtra("contentID", getIntent().getStringExtra("contentid"));
                intent.putExtra("title", "");
                startActivity(intent);
            }
        });
    }

    private void setTaxiData() {
        new AsyncTask<Void, Void, JSONObject>() {
            private String contentid;
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(getContext());
                contentid = getIntent().getStringExtra("contentid");
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {

                try {
                    return NetworkUtil.getInstance().getShareTaxiContent(contentid);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject itemes) {
                Log.d(getClass().getSimpleName(), itemes.toJSONString());

                ImageLoaderUtil.getInstance().initImageLoader();

                ViewGroup contentGroup = (ViewGroup) findViewById(R.id.share_taxi_detail_content);
                ViewGroup departureGroup = (ViewGroup) findViewById(R.id.share_taxi_detail_start_layout);
                ViewGroup destinationGroup = (ViewGroup) findViewById(R.id.share_taxi_detail_end_layout);
                ViewGroup profileInfoGroup = (ViewGroup) findViewById(R.id.share_taxi_detail_writer_profile);
                ViewGroup sharePeopleGroup = (ViewGroup) findViewById(R.id.share_taxi_detail_with_info_root_view);
                ViewGroup profileImageGroup = (ViewGroup) findViewById(R.id.share_taxi_detail_scroll_info_view);

                TextView contentTextView = (TextView) contentGroup.getChildAt(0);
                TextView departureTextView = (TextView) departureGroup.getChildAt(1);
                TextView destinationTextView = (TextView) destinationGroup.getChildAt(1);
                TextView peopleCountTextView = (TextView) findViewById(R.id.share_taxi_detail_peoplecount);
                CircleImageView profileImageView = (CircleImageView) profileInfoGroup.getChildAt(0);
                TextView writerNameView = (TextView) profileInfoGroup.getChildAt(1);
                TextView writeTimeView = (TextView) profileInfoGroup.getChildAt(2);
                TextView taxiButton = (TextView) findViewById(R.id.share_taxi_detail_button);

                String content = itemes.get("content").toString();
                String departure = itemes.get("departure").toString();
                String destination = itemes.get("destination").toString();
                String writer = itemes.get("writer").toString();
                String writername = itemes.get("writername").toString();
                String time = itemes.get("time").toString();
                String isLeave = itemes.get("isLeave").toString();
                String sharePerson = itemes.get("sharePerson").toString();
                String isSharePerson = itemes.get("isSharePerson").toString();
                JSONArray sharePersonArray = (JSONArray) itemes.get("personInfo");
                Log.d(getClass().getSimpleName(), sharePersonArray.toJSONString());

                contentTextView.setText(content);
                departureTextView.setText(departure);
                destinationTextView.setText(destination);
                peopleCountTextView.setText(String.format(getString(R.string.taxi_share_people_count), sharePerson));
                writerNameView.setText(writername);
                writeTimeView.setText(getSimpleDetailTime(time));
                ImageLoader.getInstance().displayImage(UrlList.PROFILE_THUMB_IMAGE_URL + writer,
                        profileImageView,
                        ImageLoaderUtil.getInstance().getThumbProfileImageOptions()
                );

                /* 나는 이미 택시를 타서 합승자 명단에 있어요 !! */
                if (Boolean.parseBoolean(isSharePerson)) {
                    taxiButton.setBackgroundResource(R.drawable.bg_button_share_taxi_ride);
                    taxiButton.setText(getString(R.string.taxi_share_delay_leave));
                    taxiButton.setClickable(false);
                } else {
                /* 택시를 타고 싶어요 */
                    taxiButton.setText(getString(R.string.taxi_share_title));
                    taxiButton.setBackgroundResource(R.drawable.bg_button_share_taxi_ride);
                    taxiButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /* 택시가 타고싶어요 리스너 */
                            setisWith(contentid, "1");
                        }
                    });
                }

                /* 택시가 출발 함 */
                if (isLeave.equals("1")) {
                    taxiButton.setText(getString(R.string.taxi_share_isLeave));
                    peopleCountTextView.setText(getString(R.string.taxi_share_leave));
                    taxiButton.setBackgroundResource(R.drawable.bg_button_taxi_leave);
                    taxiButton.setTextColor(getResources().getColor(R.color.share_taxi_highlight_color));
                    peopleCountTextView.setTextColor(getResources().getColor(R.color.share_taxi_highlight_color));
                    taxiButton.setClickable(false);
                }

                /* 글 작성자가 자신의 글에 들어왔는데 아직 출발을 안한 상태 */
                if (writer.equals(UserData.getInstance().getStudentNumber())) {
                    if (isLeave.equals("0")) {
                        taxiButton.setBackgroundColor(getResources().getColor(R.color.share_taxi_highlight_color));
                        taxiButton.setText(getString(R.string.taxi_share_want_leave));
                        taxiButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /* 택시 출발 하고 싶어요 버튼 */
                                setisLeave(contentid, "1");
                            }
                        });
                    }
                }

                /* 데이터 정보 보여줌 */
                if (Boolean.parseBoolean(isSharePerson)) {
                    int size = (int) ApplicationUtil.getInstance().dpToPx(31);
                    int margin = (int) ApplicationUtil.getInstance().dpToPx(4);
                    sharePeopleGroup.setVisibility(View.VISIBLE);

                    ViewGroup profileGroup = (ViewGroup) profileImageGroup.getChildAt(0);
                    ViewGroup foledGroup = (ViewGroup) findViewById(R.id.share_taxi_detail_with_folding_view);
                    profileGroup.removeAllViews();
                    foledGroup.removeAllViews();

                    /* 합승자 일경우 합승자 정보를 보여줌 */
                    for (Object object : sharePersonArray) {
                        JSONObject dataObject = (JSONObject) object;
                        String name = dataObject.get("name").toString();
                        String studentnumber = dataObject.get("studentnumber").toString();
                        String phonenumber = dataObject.get("phone").toString();

                        CircleImageView circleImageView = new CircleImageView(getContext());
                        profileGroup.addView(circleImageView);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) circleImageView.getLayoutParams();
                        layoutParams.width = size;
                        layoutParams.height = size;
                        layoutParams.setMargins(margin, 0, margin, 0);
                        circleImageView.setLayoutParams(layoutParams);

                        ImageLoader.getInstance().displayImage(UrlList.PROFILE_THUMB_IMAGE_URL + studentnumber,
                                circleImageView,
                                ImageLoaderUtil.getInstance().getThumbProfileImageOptions());

                        /* 폴더 열렸을때 뷰에 대해서 데이터 넣어 놓기 */
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.ui_sharetaxidetail_folded_card, foledGroup, false);
                        TextView foldedNameView = (TextView) view.findViewById(R.id.share_taxi_detail_with_name_view);
                        TextView foldedPhoneView = (TextView) view.findViewById(R.id.share_taxi_detail_with_phone_view);
                        CircleImageView foldedProfileView = (CircleImageView) view.findViewById(R.id.share_taxi_detail_with_circle_view);
                        ImageLoader.getInstance().displayImage(UrlList.PROFILE_THUMB_IMAGE_URL + studentnumber,
                                foldedProfileView,
                                ImageLoaderUtil.getInstance().getThumbProfileImageOptions());
                        foldedNameView.setText(name);
                        foldedPhoneView.setText(phonenumber);
                        foledGroup.addView(view);

                        view.setTag(phonenumber);
                        view.setBackgroundResource(R.drawable.bg_default_select_item_effect);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String phone = (String) v.getTag();
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phone));
                                startActivity(intent);
                            }
                        });
                    }
                }

                dialog.cancel();
            }
        }.execute();
    }

    /**
     * @param contentid 택시 게시판 아이디
     * @param isLeave   "0"은 택시 출발 안함, "1"은 택시 출발 함
     */
    private void setisLeave(final String contentid, final String isLeave) {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(getContext());
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().setShareTaxiLeave(contentid, isLeave);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject object) {
                dialog.cancel();
                if (object == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = object.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), getString(R.string.success_taxi_share_set_leave));
                }
                setTaxiData();
            }
        }.execute();
    }

    /**
     * @param contentid 택시 게시판 아이디
     * @param isWith    "0"은 택시 같이 안탐, "1"은 택시 같이 탐
     */
    private void setisWith(final String contentid, final String isWith) {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(getContext());
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().setShareTaxiWith(contentid, isWith);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject object) {
                dialog.cancel();
                if (object == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = object.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), getString(R.string.success_taxi_share_set_with));
                }
                setTaxiData();
            }
        }.execute();
    }

    @Override
    protected void setDefaultData() {

    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.SHARETAXT;
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.share_taxi_detail_with_info_root_view) {
            View locationView = findViewById(R.id.share_taxi_detail_locationview);
            final View rootView = v;
            final int screenHeight = ApplicationUtil.getInstance().getScreenHeight();

            if (isFolded) {
                Log.d(getClass().getSimpleName(), "Screen Height : " + screenHeight);
                Log.d(getClass().getSimpleName(), "LocationView Height : " + locationView.getHeight());
                Log.d(getClass().getSimpleName(), "LocationView Y : " + locationView.getY());

                ValueAnimator upWithInfoView = ValueAnimator.ofInt((int) rootView.getY(), locationView.getHeight() + (int) locationView.getY());

                upWithInfoView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int y = (int) animation.getAnimatedValue();
                        rootView.setY((float) y);
                    }
                });

                upWithInfoView.setDuration(400);
                upWithInfoView.start();

                findViewById(R.id.share_taxi_detail_scroll_info_view).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_writer_profile).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_button).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_content).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_with_folding_view).setVisibility(View.VISIBLE);

                ImageView foldingButton = (ImageView) findViewById(R.id.share_taxi_detail_folding_button);
                foldingButton.setImageResource(R.drawable.ic_unfold);
            } else {
                ValueAnimator upWithInfoView = ValueAnimator.ofInt(
                        ((int) locationView.getY() + locationView.getHeight() + rootView.getHeight()), 0);

                upWithInfoView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int y = (int) animation.getAnimatedValue();
                        rootView.setTranslationY((float) - y);
                    }
                });
                upWithInfoView.setDuration(400);
                upWithInfoView.start();

                findViewById(R.id.share_taxi_detail_scroll_info_view).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_writer_profile).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_button).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_content).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_with_folding_view).setVisibility(View.GONE);
                ImageView foldingButton = (ImageView) findViewById(R.id.share_taxi_detail_folding_button);
                foldingButton.setImageResource(R.drawable.ic_fold);
            }

            isFolded = !isFolded;
        }
    }
}