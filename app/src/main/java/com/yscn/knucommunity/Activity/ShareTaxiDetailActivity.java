package com.yscn.knucommunity.Activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
    private boolean isButton2 = false;
    private ClearProgressDialog clearProgressDialog = null;

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_sharetaxidetail);
        super.onCreate(bundle);
        setTaxiData();

        final String contentID = getIntent().getStringExtra("contentID");
        findViewById(R.id.share_taxi_detail_with_info_root_view).setOnClickListener(this);
        findViewById(R.id.share_taxi_detail_replyview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FreeBoardReplyActivity.class);
                intent.putExtra("contentID", contentID);
                intent.putExtra("title", "");
                startActivity(intent);
            }
        });

        /* 만약 onNewIntent 가 아니라 onCreate 를 타게 될때 알림 처리 */
        boolean isFromNotify = getIntent().getBooleanExtra("isFromNotify", false);
        if (isFromNotify) {
            setisLeave(contentID, "1");
        }

        /* 글이 삭제되었을때 처리 가능 */
        setOnSuccessDeleteListener(new successDeleteListener() {
            @Override
            public void successDelete() {
                /* 글이 삭제 되었을 경우 알림이 계속 존재 할 수 도 있으므로 제거 */
                cancelTaxiNotify();
            }
        });

        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setTaxiData() {
        new AsyncTask<Void, Void, JSONObject>() {
            private String contentid;

            @Override
            protected void onPreExecute() {
                showProgressDialog();
                contentid = getIntent().getStringExtra("contentID");
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
                if (itemes == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    cancelProgressDialog();
                    return;
                }
                String result = itemes.get("result").toString();
                if (result.equals("fail")) {
                    String reason = itemes.get("reason").toString();

                    /* 로그인 정보가 존재하지 않음 */
                    if (reason.equals("emptyuserinfo")) {
                        AlertToast.error(getContext(), R.string.error_empty_studentnumber_info);
                        UserData.getInstance().logoutUser();
                    }

                    /* 개시글이 존재 하지 않음 */
                    if (reason.equals("notexist")) {
                        /* 혹시 택시 알림이 계속 존재한다면 없앰 */
                        cancelTaxiNotify();
                        AlertToast.error(getContext(), getString(R.string.error_notexist_board_content));
                        finish();
                    }

                    cancelProgressDialog();
                    return;
                }

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
                TextView taxiButton2 = (TextView) findViewById(R.id.share_taxi_detail_button2);

                final String content = itemes.get("content").toString();
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
                ImageLoader.getInstance().displayImage(
                        NetworkUtil.getInstance().getProfileThumbURL(writer),
                        profileImageView,
                        ImageLoaderUtil.getInstance().getThumbProfileImageOptions()
                );

                /* 메뉴 새로고침 */
                board_studenuNumber = writer;
                invalidateOptionsMenu();

                /* 나는 이미 택시를 타서 합승자 명단에 있어요 !! */
                if (Boolean.parseBoolean(isSharePerson)) {
                    taxiButton.setBackgroundResource(R.drawable.bg_button_share_taxi_success);
                    taxiButton.setText(getString(R.string.taxi_share_want_leave));
                    taxiButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                /* 택시 출발 하고 싶어요 버튼 */
                            setisLeave(contentid, "1");
                        }
                    });
                    isButton2 = true;
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
                    isButton2 = false;
                }

                /* 글 작성자가 자신의 글에 들어왔는데 아직 출발을 안한 상태 */
                if (writer.equals(UserData.getInstance().getStudentNumber())) {
                    if (isLeave.equals("0")) {
                        taxiButton.setBackgroundResource(R.drawable.bg_button_share_taxi_success);
                        taxiButton.setText(getString(R.string.taxi_share_want_leave));
                        taxiButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /* 택시 출발 하고 싶어요 버튼 */
                                setisLeave(contentid, "1");
                            }
                        });
                    }
                    isButton2 = false;
                }


                /* isButton2 가 TRUE 일때 버튼이 보이고 택시 내리는 기능 필요 */
                taxiButton2.setVisibility(isButton2 ? View.VISIBLE : View.INVISIBLE);
                if (isButton2) {
                    taxiButton2.setText(getString(R.string.taxi_share_set_with_cancel));
                    taxiButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setisWith(contentid, "0");
                        }
                    });
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
                        setProfileImage(circleImageView, studentnumber);

                        /* 폴더 열렸을때 뷰에 대해서 데이터 넣어 놓기 */
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.ui_sharetaxidetail_folded_card, foledGroup, false);
                        TextView foldedNameView = (TextView) view.findViewById(R.id.share_taxi_detail_with_name_view);
                        TextView foldedPhoneView = (TextView) view.findViewById(R.id.share_taxi_detail_with_phone_view);
                        CircleImageView foldedProfileView = (CircleImageView) view.findViewById(R.id.share_taxi_detail_with_circle_view);
                        setProfileImage(foldedProfileView, studentnumber);
                        ApplicationUtil.getInstance().setTypeFace(view);

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

                        /* 만약 작성자가 처음 선택한 사람의 인원이라면 전화 사라짐 */
                        if (studentnumber.equals("-1")) {
                            view.findViewById(R.id.share_taxi_detail_with_call_view).setVisibility(View.GONE);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    /* 눌렀을떄 정보 없다 */
                                }
                            });
                        }
                    }
                } else {
                    /* 같은 탑승자가 아닐경우 합승자 뷰 없앰 */
                    sharePeopleGroup.setVisibility(View.GONE);
                }
                cancelProgressDialog();
            }
        }.execute();
    }

    private void cancelTaxiNotify() {
        NotificationManagerCompat.from(getContext()).cancel(0x12);
    }

    @Override
    protected void setProfileImage(ImageView imageView, String studentnumber) {
        if (!studentnumber.equals("-1")) {
            ImageLoader.getInstance().displayImage(
                    NetworkUtil.getInstance().getProfileThumbURL(studentnumber),
                    imageView,
                    ImageLoaderUtil.getInstance().getThumbProfileImageOptions());
        } else {
            ImageLoader.getInstance().displayImage("drawable://" + R.drawable.ic_profile,
                    imageView,
                    ImageLoaderUtil.getInstance().getThumbProfileImageOptions());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /* 만약 알림에서 눌러서 온거라면 자동으로 택시 출발 처리를 해줌 */
        /* PendingIntent 요청 할때 PendingIntent.FLAG_UPDATE_CURRENT 를 사용 해야함 */
        boolean isFromNotify = intent.getBooleanExtra("isFromNotify", false);
        if (isFromNotify) {
            String contentID = intent.getStringExtra("contentID");
            setisLeave(contentID, "1");
        }
        super.onNewIntent(intent);
    }

    private void showProgressDialog() {
        if (clearProgressDialog == null) {
            clearProgressDialog = new ClearProgressDialog(getContext());
        }
        if (clearProgressDialog.isShowing()) {
            clearProgressDialog.cancel();
        }
        clearProgressDialog.show();
    }

    private void cancelProgressDialog() {
        clearProgressDialog.cancel();
    }

    /**
     * @param contentid 택시 게시판 아이디
     * @param isLeave   "0"은 택시 출발 안함, "1"은 택시 출발 함
     */
    private void setisLeave(final String contentid, final String isLeave) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().setShareTaxiLeave(contentid, isLeave);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject object) {
                cancelProgressDialog();
                if (object == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = object.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), getString(R.string.success_taxi_share_set_leave));

                    /* 택시 알림 삭제 */
                    cancelTaxiNotify();
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

            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().setShareTaxiWith(contentid, isWith);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject object) {
                cancelProgressDialog();
                if (object == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = object.get("result").toString();
                if (result.equals("success")) {
                    if (isWith.equals("1")) {
                        AlertToast.success(getContext(), getString(R.string.success_taxi_share_set_with));
                    }

                    if (isWith.equals("0")) {
                        /* 원래 타고 있던 택시라면 지워야함 */
                        isButton2 = false;
                        findViewById(R.id.share_taxi_detail_button2).setVisibility(View.GONE);
                        AlertToast.success(getContext(), getString(R.string.success_taxi_share_set_unwith));
                    }
                }
                setTaxiData();
            }
        }.execute();
    }

    @Override
    protected void setDefaultData() {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (board_studenuNumber == null) {
            return false;
        }
        if (!board_studenuNumber.equals(UserData.getInstance().getStudentNumber())) {
            /* 지우기 */
            menu.getItem(0).setVisible(false);
            /* 수정 */
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.SHARETAXT;
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_pirmary_dark_color);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.share_taxi_detail_with_info_root_view) {
            View locationView = findViewById(R.id.share_taxi_detail_locationview);
            final View rootView = v;

            ValueAnimator transitionAnimation, fadeAnimation;
            if (isFolded) {
                transitionAnimation = ValueAnimator.ofInt((int) rootView.getY(), locationView.getHeight() + (int) locationView.getY());
                fadeAnimation = ValueAnimator.ofFloat(1f, 0f);

                ImageView foldingButton = (ImageView) findViewById(R.id.share_taxi_detail_folding_button);
                foldingButton.setImageResource(R.drawable.ic_unfold);
            } else {
                transitionAnimation = ValueAnimator.ofInt(((int) locationView.getY() + locationView.getHeight() + (int) rootView.getY()), 0);
                fadeAnimation = ValueAnimator.ofFloat(0f, 1f);

                ImageView foldingButton = (ImageView) findViewById(R.id.share_taxi_detail_folding_button);
                foldingButton.setImageResource(R.drawable.ic_fold);
            }

            transitionAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int y = (int) animation.getAnimatedValue();
                    if (!isFolded) {
                        rootView.setY((float) y);
                    } else {
                        rootView.setTranslationY((float) -y);
                    }
                }
            });

            fadeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    findViewById(R.id.share_taxi_detail_scroll_info_view).setAlpha(value);
                    findViewById(R.id.share_taxi_detail_writer_profile).setAlpha(value);
                    findViewById(R.id.share_taxi_detail_button).setAlpha(value);
                    findViewById(R.id.share_taxi_detail_button2).setAlpha(value);
                    findViewById(R.id.share_taxi_detail_content).setAlpha(value);
                }
            });

            /*
             * isFolded == TRUE : 점점 투명해진다. 애니메이션이 끝났을때 전부 GONE 해야함
             * isFolded == FALSE : 점점 불투명해진다. 애니메이션이 시작될때 전부 VISIBLE
            * */
            if (isFolded) {
                fadeAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.share_taxi_detail_button).setVisibility(View.GONE);
                        findViewById(R.id.share_taxi_detail_button2).setVisibility(View.GONE);
                        findViewById(R.id.share_taxi_detail_with_folding_view).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.share_taxi_detail_scroll_info_view).setVisibility(View.GONE);
                        findViewById(R.id.share_taxi_detail_writer_profile).setVisibility(View.GONE);
                        findViewById(R.id.share_taxi_detail_content).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            } else {
                fadeAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.share_taxi_detail_scroll_info_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.share_taxi_detail_writer_profile).setVisibility(View.VISIBLE);
                        findViewById(R.id.share_taxi_detail_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.share_taxi_detail_button2).setVisibility(isButton2 ? View.VISIBLE : View.GONE);
                        findViewById(R.id.share_taxi_detail_content).setVisibility(View.VISIBLE);
                        findViewById(R.id.share_taxi_detail_with_folding_view).setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            transitionAnimation.setDuration(400);
            fadeAnimation.setDuration(400);
            transitionAnimation.start();
            fadeAnimation.start();
            isFolded = !isFolded;
        }
    }
}