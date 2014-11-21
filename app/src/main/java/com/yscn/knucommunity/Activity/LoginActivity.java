package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class LoginActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

}
