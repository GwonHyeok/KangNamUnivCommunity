package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.Items.MajorDetailItems;
import com.yscn.knucommunity.R;

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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.major_detail_view);
        ArrayList<MajorDetailItems> itemses = getMajorDetailItems();

        for (MajorDetailItems majorInfo : itemses) {
            View view = LayoutInflater.from(this).inflate(R.layout.ui_majorprofessorlist, null);
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

        findViewById(R.id.major_go_page).setOnClickListener(this);
    }

    private ArrayList<MajorDetailItems> getMajorDetailItems() {
        ArrayList<MajorDetailItems> itemses = new ArrayList<MajorDetailItems>();
        itemses.add(new MajorDetailItems("강현우", "미디어정보공학", "280-3755", "hwkang@kangnam.ac.kr"));
        itemses.add(new MajorDetailItems("안영화", "컴퓨터공학", "280-3756", "yhan@kangnam.ac.kr"));
        itemses.add(new MajorDetailItems("양재형", "컴퓨터공학", "280-3757", "jhyang@kangnam.ac.kr"));
        itemses.add(new MajorDetailItems("조승호", "컴퓨터공학", "280-3758", "shcho@kangnam.ac.kr"));
        itemses.add(new MajorDetailItems("김태권", "컴퓨터공학", "280-3759", "ktg@kangnam.ac.kr"));
        itemses.add(new MajorDetailItems("안정호", "미디어정보공학", "280-3661", "jungho@kangnam.ac.kr"));
        itemses.add(new MajorDetailItems("양형규", "미디어정보공학", "280-3760", "hkyang@kangnam.ac.kr"));
        itemses.add(new MajorDetailItems("주영도", "컴퓨터공학", "280-3699", "ydjoo@kangnam.ac.kr"));
        return itemses;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.major_go_page) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(majorHomepage)));
        }
    }
}
