package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class GreenLightDetailActivity extends ActionBarActivity implements View.OnClickListener {
    private String m_ContentID;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_greenlightdetail);
        setDefailtData();
        viewInit();
        setContent();
    }

    private void setDefailtData() {
//        intent.putExtra("contentID", listItems.getContentid());
//        intent.putExtra("writerName", listItems.getName());
//        intent.putExtra("writerStudentNumber", listItems.getStudentnumber());
//        intent.putExtra("title", listItems.getTitle());
//        intent.putExtra("time", listItems.getTime());
        m_ContentID = getIntent().getStringExtra("contentID");
        ((TextView) findViewById(R.id.greenlight_detail_title)).setText(getIntent().getStringExtra("title"));
        ((TextView) findViewById(R.id.greenlight_detail_time)).setText(getSimpleTime(getIntent().getStringExtra("time")));
    }

    private Context getContext() {
        return this;
    }

    private void setContent() {
        new AsyncTask<Void, Void, Void>() {
            private ClearProgressDialog clearProgressDialog;
            private String content;
            private HashMap<String, String> greenLightResult;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    content = NetworkUtil.getInstance().getDefaultboardContent(m_ContentID);
                    greenLightResult = NetworkUtil.getInstance().getGreenLightResult(m_ContentID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void value) {
                if (content != null && greenLightResult != null) {
                    ((TextView) findViewById(R.id.greenlight_detail_content)).setText(content);

                    String isChecked = greenLightResult.get("isChecked");

                    Log.d(getClass().getSimpleName(), "isChecked : " + isChecked);
                    Log.d(getClass().getSimpleName(), "contentID : " + m_ContentID);

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

    private void setGreenLightOn(String positiveSize, String negativeSize) {
        TextView lightOn = (TextView) findViewById(R.id.greenlight_light_on);
        TextView lightOff = (TextView) findViewById(R.id.greenlight_light_off);
        lightOn.setBackgroundResource(R.drawable.ic_light_on_pressed);
        lightOff.setBackgroundResource(R.drawable.ic_light_off_pressed);

        lightOn.setText(String.valueOf(positiveSize));
        lightOff.setText(String.valueOf(negativeSize));
        lightOff.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        lightOn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

//        lightOn.setClickable(false);
//        lightOff.setClickable(false);
    }

    private String getSimpleTime(String defaulttime) {
        String dataTimeFormat = "yyyy-MM-dd hh:mm:ss";
        String newDateTimeFormat = "yyyy.MM.dd hh:mm";
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

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFFFAFAFA);
        }

         /* 액션바 */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* 그린라이트 버튼 */
        findViewById(R.id.greenlight_light_on).setOnClickListener(this);
        findViewById(R.id.greenlight_light_off).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reply_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.greenlight_light_on) {
            clickGreenRightButton(true);
        } else if (id == R.id.greenlight_light_off) {
            clickGreenRightButton(false);
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
