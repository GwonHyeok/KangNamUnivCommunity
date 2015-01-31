package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.SchoolRestrauntItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class SchoolRestrauntDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_restrauntdetail);
        viewInit();
        setRestrauntFoodInfo(getIntent().getStringExtra("location"));
    }

    private Context getContext() {
        return SchoolRestrauntDetailActivity.this;
    }

    private void setRestrauntFoodInfo(String restraunt) {
        new AsyncTask<String, Void, ArrayList<SchoolRestrauntItems>>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    dialog = new ClearProgressDialog(getContext());
                    dialog.show();
                } else {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(false);
                }
            }

            @Override
            protected ArrayList<SchoolRestrauntItems> doInBackground(String... strings) {
                String str_restaunt = strings[0];
                NetworkUtil.SchoolRestraunt schoolRestraunt = null;
                if (str_restaunt.equals("shal")) {
                    schoolRestraunt = NetworkUtil.SchoolRestraunt.SHAL;
                } else if (str_restaunt.equals("gisuk")) {
                    schoolRestraunt = NetworkUtil.SchoolRestraunt.GISUK;
                } else if (str_restaunt.equals("insa")) {
                    schoolRestraunt = NetworkUtil.SchoolRestraunt.INSA;
                } else if (str_restaunt.equals("gyung")) {
                    schoolRestraunt = NetworkUtil.SchoolRestraunt.GYUNG;
                } else {
                    schoolRestraunt = NetworkUtil.SchoolRestraunt.SHAL;
                }
                try {
                    return NetworkUtil.getInstance().getRestrauntInfo(schoolRestraunt);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<SchoolRestrauntItems> itemes) {
                if (itemes != null) {
                    StringBuilder builder = new StringBuilder();
                    for (SchoolRestrauntItems iteme : itemes) {
                        builder.append(iteme.getFoodName()).append(" : ").append(iteme.getFoodPrice()).append("\n");
                    }
                    TextView textView = (TextView) findViewById(R.id.restrauntdetail_text);
                    textView.setText(builder.toString());
                } else {
                    AlertToast.error(getContext(), R.string.error_to_work);
                }
                dialog.cancel();
            }
        }.execute(restraunt);
    }

    private void viewInit() {
        /* set Toolbar */
        Intent intent = getIntent();
        int color = intent.getIntExtra("color", 0xFF0097A7);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getActionBarTitle());
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(color);
            getWindow().setNavigationBarColor(color);
        }
    }

    private String getActionBarTitle() {
        Intent intent = getIntent();
        String location = intent.getStringExtra("location");
        if (location.equals("shal")) {
            return "샬롬관";
        } else if (location.equals("gisuk")) {
            return "기숙사";
        } else if (location.equals("gyung")) {
            return "경천관";
        } else if (location.equals("insa")) {
            return "인사관";
        } else {
            return "";
        }
    }
}
