package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.yscn.knucommunity.R;


/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class SchoolRestrauntActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_restraunt);
        findViewById(R.id.schoolrestraunt_shal).setOnClickListener(this);
        findViewById(R.id.schoolrestraunt_insa).setOnClickListener(this);
        findViewById(R.id.schoolrestraunt_gyung).setOnClickListener(this);
        findViewById(R.id.schoolrestraunt_gisuk).setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.school_restraunt_list_primary_dark_color));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(this, SchoolRestrauntDetailActivity.class);
        if (id == R.id.schoolrestraunt_shal) {
            intent.putExtra("location", "shal");
        } else if (id == R.id.schoolrestraunt_gisuk) {
            intent.putExtra("location", "gisuk");
        } else if (id == R.id.schoolrestraunt_insa) {
            intent.putExtra("location", "insa");
        } else if (id == R.id.schoolrestraunt_gyung) {
            intent.putExtra("location", "gyung");
        }
        startActivity(intent);
    }
}
