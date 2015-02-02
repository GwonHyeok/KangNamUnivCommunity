package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.NotifyFooterScrollView;
import com.yscn.knucommunity.Items.ShareTaxiListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Ui.ShareTaxiPagerAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 1. 20..
 */
public class ShareTaxiActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private String mDate[][];
    private String mYear[];
    private ViewPager viewPager;
    private int pageIndex = 1, WRITE_RESPONSE_CODE = 0X10;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_sharetaxi);
        toolbarInit();

        // Adapter 에서 필요로 하는 날자 정보를 저장해놓음
        setDayArray();

        // Viewpager init
        viewPager = (ViewPager) findViewById(R.id.share_taxi_viewpager);
        viewPager.setAdapter(new ShareTaxiPagerAdapter(this, mDate));
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(this);

        // 상단바 투명 KITKAT 이상부터
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        findViewById(R.id.share_taxi_nextday).setOnClickListener(this);
        findViewById(R.id.share_taxi_yesterday).setOnClickListener(this);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.share_taxi_root);
        View backView = viewGroup.getChildAt(0);

        int width = ApplicationUtil.getInstance().getScreenWidth();
        int height = (int) ApplicationUtil.getInstance().dpToPx(205);

        Bitmap bitmap = ApplicationUtil.getInstance().decodeSampledBitmap(
                getResources(),
                R.drawable.bg_sharetaxi,
                width,
                height
        );

        if (Build.VERSION.SDK_INT >= 16) {
            backView.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            backView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }

        checkHasPhoneNumber();
        setTaxiData();
        scrollViewInit();
    }

    private void scrollViewInit() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.board_list_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadTaxiData();
            }
        });
        final NotifyFooterScrollView scrollView = (NotifyFooterScrollView) findViewById(R.id.share_taxi_scrollview);
        scrollView.setonScrollToBottomListener(new NotifyFooterScrollView.onScrollToBottomListener() {
            @Override
            public void scrollToBottom() {
                View view = scrollView.getChildAt(0);
                if (view instanceof LinearLayout) {
                    int childSize = ((LinearLayout) view).getChildCount();
                    if (childSize == pageIndex * 15) {
                        pageIndex = (pageIndex + 1);
                        setTaxiData();
                    }
                }
            }
        });
    }

    private void checkHasPhoneNumber() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog clearprogressdialog;

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    clearprogressdialog = new ClearProgressDialog(getContext());
                    clearprogressdialog.show();
                } else {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(false);
                }
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().getPhoneNumber();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                clearprogressdialog.cancel();
                if (jsonObject == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = jsonObject.get("result").toString();

                // 성공했을 경우
                if (result.equals("success")) {
                    String phonenumber = jsonObject.get("data").toString();

                    // 핸드폰 번호가 없으면 폰 번호 액티비티 실행
                    if (phonenumber.isEmpty()) {
                        finish();
                        startActivity(new Intent(getContext(), PhoneNumberInputActivity.class));
                    } else {
                        UserData.getInstance().setPhoneNumber(phonenumber);
                    }
                }
            }
        }.execute();
    }

    private Context getContext() {
        return this;
    }

    private void setTaxiData() {
        new AsyncTask<Void, Void, ArrayList<ShareTaxiListItems>>() {
            private String time;
            private ClearProgressDialog clearProgressdialog;

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    clearProgressdialog = new ClearProgressDialog(getContext());
                    clearProgressdialog.show();
                    int position = viewPager.getCurrentItem();
                    String day = mDate[position][1];
                    String month = mDate[position][0];
                    String year = mYear[position];
                    time = year + "-" + month + "-" + day;
                } else {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(false);
                }
            }

            @Override
            protected ArrayList<ShareTaxiListItems> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getShareTaxiList(time, pageIndex);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<ShareTaxiListItems> itemes) {
                if (itemes == null) {
                    clearProgressdialog.cancel();
                    swipeRefreshLayout.setRefreshing(false);
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.share_taxi_data_view);
                ImageLoaderUtil.getInstance().initImageLoader();

                for (ShareTaxiListItems item : itemes) {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.ui_sharetaxilist, linearLayout, false);
                    TextView departureView = (TextView) view.findViewById(R.id.share_taxi_start_locaction_textview);
                    TextView destinationView = (TextView) view.findViewById(R.id.share_taxi_stop_locaction_textview);
                    TextView timeView = (TextView) view.findViewById(R.id.share_taxi_time_textview);
                    TextView personCountView = (TextView) view.findViewById(R.id.textView12);
                    ViewGroup thumbnailView = (ViewGroup) view.findViewById(R.id.ui_sharetaxilist_shareperson_thumbnail);

                    destinationView.setSelected(true);
                    departureView.setSelected(true);

                    destinationView.setText(item.getDestination());
                    departureView.setText(item.getDeparture());
                    timeView.setText(getTime(item.getDeparturetime()));
                    personCountView.setText(String.valueOf(item.getShareperson().length + 1));
                    personCountView.setBackgroundResource(item.getIsLeave().equals("0") ?
                            R.drawable.bg_sharetaxi_general : R.drawable.bg_button_share_taxi_success);

                    // 31dp 양옆 마진 4dp
                    // 탑승자 이미지 뷰
                    int imageViewSize = (int) ApplicationUtil.getInstance().dpToPx(31);
                    int margin = (int) ApplicationUtil.getInstance().dpToPx(4);

                    // 본인을 합승자 이미지에 보이게
                    CircleImageView taxtRootView = (CircleImageView) view.findViewById(R.id.ui_sharetaxtlist_rootperson_thumbnail);
                    ImageLoader.getInstance().displayImage(
                            UrlList.PROFILE_THUMB_IMAGE_URL + item.getWriter(),
                            taxtRootView,
                            ImageLoaderUtil.getInstance().getDefaultOptions());

                    for (String person : item.getShareperson()) {
                        CircleImageView imageView = new CircleImageView(getContext());

                        /* 이미 있는 사람일 경우 사람 정보가 -1 로 넘어온다. */
                        if (!person.equals("-1")) {
                            ImageLoader.getInstance().displayImage(
                                    UrlList.PROFILE_THUMB_IMAGE_URL + person,
                                    imageView,
                                    ImageLoaderUtil.getInstance().getThumbProfileImageOptions());
                        } else {
                            ImageLoader.getInstance().displayImage(
                                    "drawable://" + R.drawable.ic_profile,
                                    imageView,
                                    ImageLoaderUtil.getInstance().getThumbProfileImageOptions());
                        }

                        thumbnailView.addView(imageView);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                        layoutParams.height = imageViewSize;
                        layoutParams.width = imageViewSize;
                        layoutParams.setMargins(margin, 0, margin, 0);
                        imageView.setLayoutParams(layoutParams);
                    }

                    view.setBackgroundResource(R.drawable.bg_default_select_item_effect);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShareTaxiListItems tag = (ShareTaxiListItems) v.getTag();
                            Intent intent = new Intent(ShareTaxiActivity.this, ShareTaxiDetailActivity.class);
                            intent.putExtra("writerStudentNumber", tag.getWriter());
                            intent.putExtra("contentID", tag.getContentid());
                            intent.putExtra("isLeave", tag.getIsLeave());
                            startActivity(intent);
                        }
                    });
                    view.setTag(item);
                    linearLayout.addView(view);
                }
                clearProgressdialog.cancel();
                swipeRefreshLayout.setRefreshing(false);
            }
        }.execute();
    }

    /*
     *        월    일
     *      ["1"]["27"] : [00] [01]
     *      ["1"]["28"] : [10] [11]
     *      ["2"]["01"] : [20] [21]
     *      ["2"]["02"] : [30] [31]
     */
    private void setDayArray() {
        mDate = new String[4][2];
        mYear = new String[4];
        Date now_date = Calendar.getInstance().getTime();

        // 하루 전날로 돌린다
        now_date.setTime(now_date.getTime() - 1000 * 60 * 60 * 24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now_date);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                if (j == 0) {
                    mDate[i][j] = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                } else if (j == 1) {
                    mDate[i][j] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                }
            }
            mYear[i] = String.valueOf(calendar.get(Calendar.YEAR));

            // 다음날자로 돌린다.
            now_date.setTime(now_date.getTime() + 1000 * 60 * 60 * 24);
            calendar.setTime(now_date);
        }
    }

    private void reloadTaxiData() {
        pageIndex = 1;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.share_taxi_data_view);
        linearLayout.removeAllViews();
        setTaxiData();
    }

    private void toolbarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getTime(String src_time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat new_format = new SimpleDateFormat("hh:mm a");
        String dst_time = "";
        try {
            Date newDate = format.parse(src_time);
            dst_time = new_format.format(newDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dst_time;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return true;
    }

    @Override
    public void onActivityResult(int responesCode, int resultCode, Intent data) {
        if (responesCode == WRITE_RESPONSE_CODE && resultCode == RESULT_OK) {
            reloadTaxiData();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_write) {
            startActivityForResult(new Intent(getContext(), ShareTaxiWriteActivity.class), WRITE_RESPONSE_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TextView dateView = (TextView) findViewById(R.id.share_taxi_date_textview);
        dateView.setText(
                String.format(getString(R.string.sharetaxi_time_format),
                        mDate[position][0],
                        mYear[position])
        );
        reloadTaxiData();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.share_taxi_nextday) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        } else if (id == R.id.share_taxi_yesterday) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        }
    }
}
