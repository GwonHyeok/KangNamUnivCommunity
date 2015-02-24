package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 22..
 */
public class ExternalLinkActivity extends ActionBarActivity implements View.OnClickListener {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_external_link);

        Bitmap bitmap = ApplicationUtil.getInstance().decodeSampledBitmap(
                getResources(),
                R.drawable.bg_out_acitivity,
                ApplicationUtil.getInstance().getScreenWidth(),
                ApplicationUtil.getInstance().getScreenHeight()
        );

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT >= 16) {
            findViewById(R.id.external_link_root).setBackground(bitmapDrawable);
        } else {
            findViewById(R.id.external_link_root).setBackgroundDrawable(bitmapDrawable);
        }

        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        findViewById(R.id.external_link_externalscholarInfo).setOnClickListener(this);
        findViewById(R.id.external_link_contestInfo).setOnClickListener(this);
        findViewById(R.id.external_link_externalactivityInfo).setOnClickListener(this);

        ApplicationUtil.getInstance().setTypeFace(findViewById(R.id.external_link_root));
    }

    private void startWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.external_link_externalscholarInfo:
                startWeb("http://mbanote2.tistory.com/37");
                break;
            case R.id.external_link_contestInfo:
                startWeb("http://contests.saramin.co.kr/contests");
                break;
            case R.id.external_link_externalactivityInfo:
                startWeb("http://contests.saramin.co.kr/outreach");
                break;
        }
    }
}
