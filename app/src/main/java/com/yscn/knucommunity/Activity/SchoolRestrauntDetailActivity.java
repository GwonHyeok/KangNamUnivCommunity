package com.yscn.knucommunity.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yscn.knucommunity.Items.SchoolRestrauntItems;
import com.yscn.knucommunity.R;
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
        viewInit();
        setContentView(R.layout.activity_restrauntdetail);
        setRestrauntFoodInfo(getIntent().getStringExtra("location"));
    }

    private Context getContext() {
        return SchoolRestrauntDetailActivity.this;
    }

    private void setRestrauntFoodInfo(String restraunt) {
        new AsyncTask<String, Void, ArrayList<SchoolRestrauntItems>>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getContext());
                dialog.setIndeterminate(true);
                dialog.show();
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
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<SchoolRestrauntItems> itemes) {
                StringBuilder builder = new StringBuilder();
                for (SchoolRestrauntItems iteme : itemes) {
                    builder.append(iteme.getFoodName()).append(" : ").append(iteme.getFoodPrice()).append("\n");
                }
                TextView textView = (TextView) findViewById(R.id.restrauntdetail_text);
                textView.setText(builder.toString());
                dialog.cancel();
            }
        }.execute(restraunt);
    }

    private void viewInit() {
        /* 액션바 */
        Intent intent = getIntent();
        int color = intent.getIntExtra("color", 0xFF0097A7);
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_base, null);

        ((TextView) view.findViewById(R.id.actionbar_base_title)).setText(getActionBarTitle());
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
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
