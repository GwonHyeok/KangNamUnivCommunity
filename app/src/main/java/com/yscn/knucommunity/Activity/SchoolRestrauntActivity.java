package com.yscn.knucommunity.Activity;

import android.content.Intent;
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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(this, SchoolRestrauntDetailActivity.class);
        if (id == R.id.schoolrestraunt_shal) {
            intent.putExtra("color", 0xFF42A5F5);
            intent.putExtra("location", "shal");
        } else if (id == R.id.schoolrestraunt_gisuk) {
            intent.putExtra("color", 0xFF0D47A1);
            intent.putExtra("location", "gisuk");
        } else if (id == R.id.schoolrestraunt_insa) {
            intent.putExtra("color", 0xFF1E88E5);
            intent.putExtra("location", "insa");
        } else if (id == R.id.schoolrestraunt_gyung) {
            intent.putExtra("color", 0xFF1565C0);
            intent.putExtra("location", "gyung");
        }
        startActivity(intent);
    }
}
