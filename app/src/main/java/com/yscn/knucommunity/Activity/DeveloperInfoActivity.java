package com.yscn.knucommunity.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 15. 2. 19..
 */
public class DeveloperInfoActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_developerinfo);

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
