package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.MajorDetailItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.util.ArrayList;
import java.util.Random;

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

        setRandomColor();

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

        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setRandomColor() {
        Random random = new Random();
        int index = random.nextInt(7);

        int[] background = getResources().getIntArray(R.array.background_studentground_detail_list);
        int[] primarydark = getResources().getIntArray(R.array.background_dark_studentground_detail_list);
        int[] highlight = getResources().getIntArray(R.array.highlight_studentground_detail_list);
        int[] dull = getResources().getIntArray(R.array.dull_studentground_detail_list);

        /* Background Color */
        View bg_view = findViewById(R.id.relativeLayout2);
        bg_view.setBackgroundColor(background[index]);

        /* Highlight Color */
        TextView title = (TextView) findViewById(R.id.textview);
        View line_view = findViewById(R.id.line_view);
        title.setTextColor(highlight[index]);
        line_view.setBackgroundColor(highlight[index]);

        /* Go Homepage */
        ImageButton button = (ImageButton) findViewById(R.id.major_go_page);
        int res = getResources().getIdentifier("ic_gopage_" + index, "drawable", getPackageName());
        button.setImageResource(res);

        /* Dull Color */
        TextView homepageView = (TextView) findViewById(R.id.major_homepage_url);
        View line_view2 = findViewById(R.id.line_view2);
        homepageView.setTextColor(dull[index]);
        line_view2.setBackgroundColor(dull[index]);

        /* set Status Bar Color */
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(primarydark[index]);
            getWindow().setNavigationBarColor(primarydark[index]);
        }
    }

    private void setProfessorData() {
        new AsyncTask<Void, Void, ArrayList<MajorDetailItems>>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(getContext());
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
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.ui_majorprofessorlist, linearLayout, false);
                        linearLayout.addView(view);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                        params.setMargins(18, 10, 18, 2);
                        view.setLayoutParams(params);
                        TextView nameTextView = (TextView) view.findViewById(R.id.professor_name);
                        TextView majorTextView = (TextView) view.findViewById(R.id.professor_major);
                        TextView phoneTextView = (TextView) view.findViewById(R.id.professor_phone);
                        TextView emailTextView = (TextView) view.findViewById(R.id.professor_email);
                        nameTextView.setText(String.format(getString(R.string.base_text_professor_name), majorInfo.getName()));
                        majorTextView.setText(String.format(getString(R.string.base_text_professor_major), majorInfo.getMajor()));
                        phoneTextView.setText(String.format(getString(R.string.base_text_professor_phone), majorInfo.getPhone()));
                        emailTextView.setText(String.format(getString(R.string.base_text_professor_email), majorInfo.getEmail()));
                        ApplicationUtil.getInstance().setTypeFace(view);
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
            if (majorHomepage.isEmpty()) {
                AlertToast.warning(getContext(), getString(R.string.warning_no_homepage));
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(majorHomepage)));
            }
        }
    }
}
