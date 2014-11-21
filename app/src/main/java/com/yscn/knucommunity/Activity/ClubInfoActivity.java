package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yscn.knucommunity.R;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class ClubInfoActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        viewInit();
        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getMajorList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setDivider(new ColorDrawable(0xFFBDBDBD));
        listView.setDividerHeight(1);
        setContentView(listView);
    }

    public ArrayList<String> getMajorList() {
        ArrayList<String> majorList = new ArrayList<String>();
        majorList.add("날개");
        majorList.add("아르니아");
        majorList.add("올클리어");
        majorList.add("천국의 소리");
        majorList.add("시내터");
        majorList.add("마당지기");
        majorList.add("더씨");
        majorList.add("플로우데블러");
        majorList.add("스콥스");
        majorList.add("산악부");
        majorList.add("인트릭션");
        majorList.add("파우더");
        majorList.add("아타락시아");
        majorList.add("외침");
        majorList.add("한아름");
        majorList.add("원(자원봉사)");
        majorList.add("스카우트");
        majorList.add("젊은 새 이웃");
        majorList.add("사회복지 연구회");
        majorList.add("VCC (자원 복지연구회)");
        majorList.add("비누방울 (목욕보조)");
        majorList.add("사랑의손");
        majorList.add("굿네이버스");
        majorList.add("로타렉트");
        return majorList;
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFFD81B60);
        }

        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_base, null);
        ((TextView) view.findViewById(R.id.actionbar_base_title)).setText("중앙 동아리");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD81B60")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
        view.findViewById(R.id.actionbar_base_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClubInfoActivity.this.finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(this, ClubDetailActivity.class));
    }
}
