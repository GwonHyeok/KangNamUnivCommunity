package com.yscn.knucommunity.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

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
    }

    private void greenLightButtonInit() {
        /* 그린라이트 버튼 */
        findViewById(R.id.greenlight_light_on).setOnClickListener(this);
        findViewById(R.id.greenlight_light_off).setOnClickListener(this);
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
    protected int getStatusBarColor() {
        return 0xFFFAFAFA;
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
