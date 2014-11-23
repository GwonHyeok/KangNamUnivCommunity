package com.yscn.knucommunity.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.Items.MajorDetailItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MajorDetailActivity extends ActionBarActivity implements View.OnClickListener {
    private String majorName;
    private String majorHomepage;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_majordetail);
        getSupportActionBar().hide();

        /* set Default Info (majorName, majorHomepage, majorType */
        majorName = getIntent().getStringExtra("majorName");
        majorHomepage = getIntent().getStringExtra("majorHomePage");
        TextView majorNameView = (TextView) findViewById(R.id.major_name);
        TextView majorHomePageView = (TextView) findViewById(R.id.major_homepage_url);
        majorNameView.setText(majorName);
        majorHomePageView.setText(majorHomepage);

        /* add Professor Info Tab */
        setProfessorData();

        /* set Click Listener */
        findViewById(R.id.major_go_page).setOnClickListener(this);
    }

    private void setProfessorData() {
        new AsyncTask<Void, Void, ArrayList<MajorDetailItems>>() {
            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getContext());
                dialog.setIndeterminate(true);
                dialog.show();
            }

            @Override
            protected ArrayList<MajorDetailItems> doInBackground(Void... voids) {
                try {
                    int majorType = -1;
                    String str_majorType = getIntent().getStringExtra("majorType");
                    if (str_majorType != null) {
                        majorType = Integer.parseInt(str_majorType);
                    }
                    return NetworkUtil.getInstance().getMajorDetailInfo(majorType);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<MajorDetailItems> itemses) {
                if (itemses != null) {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.major_detail_view);
                    for (MajorDetailItems majorInfo : itemses) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.ui_majorprofessorlist, null);
                        linearLayout.addView(view);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                        params.setMargins(30, 5, 30, 5);
                        view.setLayoutParams(params);
                        TextView nameTextView = (TextView) view.findViewById(R.id.professor_name);
                        TextView majorTextView = (TextView) view.findViewById(R.id.professor_major);
                        TextView phoneTextView = (TextView) view.findViewById(R.id.professor_phone);
                        TextView emailTextView = (TextView) view.findViewById(R.id.professor_email);
                        nameTextView.setText(String.format(getString(R.string.base_text_professor_name), majorInfo.getName()));
                        majorTextView.setText(String.format(getString(R.string.base_text_professor_major), majorInfo.getMajor()));
                        phoneTextView.setText(String.format(getString(R.string.base_text_professor_phone), majorInfo.getPhone()));
                        emailTextView.setText(String.format(getString(R.string.base_text_professor_email), majorInfo.getEmail()));
                    }
                } else {
                    /* Exception */
                }
                dialog.cancel();
            }
        }.execute();
    }

    private Context getContext() {
        return this;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.major_go_page) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(majorHomepage)));
        }
    }
}
