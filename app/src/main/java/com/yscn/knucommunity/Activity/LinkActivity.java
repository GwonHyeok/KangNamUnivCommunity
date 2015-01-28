package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class LinkActivity extends MenuBaseActivity implements View.OnClickListener {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_link);
        viewInit();
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xFF545486);
        }
        getSupportActionBar().hide();
        findViewById(R.id.link_app_knu).setOnClickListener(this);
        findViewById(R.id.link_app_knu_sugang).setOnClickListener(this);
        findViewById(R.id.link_app_knu_timetable).setOnClickListener(this);
        findViewById(R.id.link_homepage_eclass).setOnClickListener(this);
        findViewById(R.id.link_homepage_knu).setOnClickListener(this);
        findViewById(R.id.link_homepage_sugang).setOnClickListener(this);

        View view = findViewById(R.id.link_activity_root);
        Bitmap bitmap = ApplicationUtil.getInstance().decodeSampledBitmap(
                getResources(),
                R.drawable.bg_link,
                ApplicationUtil.getInstance().getScreenWidth(),
                ApplicationUtil.getInstance().getScreenHeight()
        );

        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.link_app_knu) {
            startActivity(getApplicationIntent(LINK.APP_KNU));
        } else if (id == R.id.link_app_knu_sugang) {
            startActivity(getApplicationIntent(LINK.APP_SUGANG));
        } else if (id == R.id.link_app_knu_timetable) {
            startActivity(getApplicationIntent(LINK.APP_TIMETABLE));
        } else if (id == R.id.link_homepage_eclass) {
            startActivity(getHomepageIntent(LINK.HOMEPAGE_ECLASS));
        } else if (id == R.id.link_homepage_sugang) {
            startActivity(getHomepageIntent(LINK.HOMEPAGE_SUGANG));
        } else if (id == R.id.link_homepage_knu) {
            startActivity(getHomepageIntent(LINK.HOMEPAGE_KNU));
        }
    }

    public Intent getHomepageIntent(LINK link) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(getHomepageLink(link)));
    }

    public Intent getApplicationIntent(LINK link) {
        Intent i = null;
        PackageManager manager = getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage(getApplicationPackageName(link));
            if (i == null)
                throw new PackageManager.NameNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER);
        } catch (PackageManager.NameNotFoundException e) {
            i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id=" + getApplicationPackageName(link)));
        }
        return i;
    }

    private String getHomepageLink(LINK link) {
        String URL = null;
        if (link == LINK.HOMEPAGE_ECLASS) {
            URL = "http://eclass.kangnam.ac.kr";
        } else if (link == LINK.HOMEPAGE_KNU) {
            URL = "http://kangnam.ac.kr";
        } else if (link == LINK.HOMEPAGE_SUGANG) {
            URL = "http://sugang.kangnam.ac.kr";
        } else if (link == LINK.HOMEPAGE_SUB_KNU) {
            URL = "http://web.kangnam.ac.kr/about/uni/info_subject.jsp";
        }
        return URL;
    }

    public String getApplicationPackageName(LINK link) {
        String packageName = null;
        if (link == LINK.APP_KNU) {
            packageName = "kr.ac.kangnam.knusmart";
        } else if (link == LINK.APP_SUGANG) {
            packageName = "kr.co.swit.knuv";
        } else if (link == LINK.APP_TIMETABLE) {
            packageName = "com.hyeok.kangnamunivtimetable";
        }
        return packageName;
    }

    private enum LINK {APP_KNU, APP_SUGANG, APP_TIMETABLE, HOMEPAGE_ECLASS, HOMEPAGE_KNU, HOMEPAGE_SUB_KNU, HOMEPAGE_SUGANG}
}
