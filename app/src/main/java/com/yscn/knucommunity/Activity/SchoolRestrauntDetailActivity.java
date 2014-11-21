package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class SchoolRestrauntDetailActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        viewInit();
        setContentView(R.layout.activity_restrauntdetail);
        TextView textView = (TextView) findViewById(R.id.restrauntdetail_text);
        textView.setText(getRestrauntFood());
    }

    private String getRestrauntFood() {
        return ("해장라면 : 3800\n" + "틈새라면 : 3500\n" + "떡만두라면 : 3000\n" + "치즈라면 : 2800\n" + "라면 : 2500\n" + "뚝배기불고기 : 5000\n" + "재육덥밥 : 4500\n" + "카래덥밥 : 4500\n" + "김치볶음밥 : 4500\n" + "야채볶음밥 : 4000\n" + "비빔밥 : 4000\n" + "손수재비 : 4500\n" + "칼국수 : 4000\n" + "치즈라볶이 : 4500\n" + "라볶이 : 4000\n" + "쫄면 : 4000\n" + "떡만두국 : 5000\n" + "만두국 : 5000\n" + "떡국 : 4500\n" + "물냉면 : 3500\n" + "비빔냉면 : 3500\n" + "참치김밤 : 2500\n" + "김밥 : 2000\n" + "치즈돈까스 : 5000\n" + "카래돈까스 : 5000\n" + "돈까스 : 4500\n");

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
