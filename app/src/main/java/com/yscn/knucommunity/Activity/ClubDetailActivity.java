package com.yscn.knucommunity.Activity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Random;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class ClubDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_clubdetail);
        viewInit();
        setRandomColor();
        setClubDetailInfo();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setClubDetailInfo() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ProgressBar progressbar;

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getApplicationContext(), R.string.error_check_network_state);
                    cancel(true);
                    return;
                }

                progressbar = (ProgressBar) findViewById(R.id.progressbar);
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                String clubid = getIntent().getStringExtra("clubid");
                try {
                    return NetworkUtil.getInstance().getSchoolClubDetail(clubid);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onPostExecute(JSONObject jsonobject) {
                progressbar.setVisibility(View.GONE);
                if (jsonobject == null) {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                    return;
                }

                String result = jsonobject.get("result").toString();

                if (result.equals("success")) {
                    String clubname = getIntent().getStringExtra("clubname");
                    String summary = jsonobject.get("summary").toString();
                    String chairman = jsonobject.get("chairman").toString();
                    String vicechairman = jsonobject.get("vicechairman").toString();
                    String detail = jsonobject.get("detail").toString();

                    TextView clubNameView = (TextView) findViewById(R.id.clubdetail_clubname);
                    TextView summaryView = (TextView) findViewById(R.id.clubdetail_clubsummary);
                    TextView chairmanView = (TextView) findViewById(R.id.clubdetail_chairman);
                    TextView vicechairmanView = (TextView) findViewById(R.id.clubdetail_vicechairman);
                    TextView detailView = (TextView) findViewById(R.id.clubdetail_detail);

                    clubNameView.setText(clubname);
                    summaryView.setText(summary);
                    chairmanView.setText(chairman);
                    vicechairmanView.setText(vicechairman);
                    detailView.setText(detail);

                } else if (result.equals("fail")) {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                }
            }
        }.execute();
    }

    private void viewInit() {
        /* Hide Action Bar */
        getSupportActionBar().hide();
    }

    private void setRandomColor() {
        Random random = new Random();
        int index = random.nextInt(7);

        int[] primarydark = getResources().getIntArray(R.array.background_dark_studentground_detail_list);
        int[] background = getResources().getIntArray(R.array.background_studentground_detail_list);
        int[] highlight = getResources().getIntArray(R.array.highlight_studentground_detail_list);
        int[] dull = getResources().getIntArray(R.array.dull_studentground_detail_list);

        /* Background Color */
        View bg_view = findViewById(R.id.relativeLayout2);
        bg_view.setBackgroundColor(background[index]);

        /* Highlight Color */
        TextView title = (TextView) findViewById(R.id.textView3);
        View line_view = findViewById(R.id.textView2);
        title.setTextColor(highlight[index]);
        line_view.setBackgroundColor(highlight[index]);

        /* Dull Color */
        TextView homepageView = (TextView) findViewById(R.id.textView5);
        TextView infoView = (TextView) findViewById(R.id.clubdetail_clubsummary);
        View line_view2 = findViewById(R.id.view);
        homepageView.setTextColor(dull[index]);
        line_view2.setBackgroundColor(dull[index]);
        infoView.setTextColor(dull[index]);

        /* set Status Bar Color */
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(primarydark[index]);
        }
    }
}
