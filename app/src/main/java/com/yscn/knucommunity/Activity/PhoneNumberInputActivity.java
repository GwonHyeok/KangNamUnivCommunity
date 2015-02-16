package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 15. 1. 27..
 */
public class PhoneNumberInputActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_phoneinput);
        setBackground();
        setToolbar();

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        findViewById(R.id.phoneinput_cornfirm).setOnClickListener(this);
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setBackground() {
        int phoneWidth = ApplicationUtil.getInstance().getScreenWidth();
        int phoneHeight = ApplicationUtil.getInstance().getScreenHeight();

        Bitmap bitmap = ApplicationUtil.getInstance().decodeSampledBitmap(
                getResources(),
                R.drawable.bg_sharetaxiphoneinput,
                phoneWidth,
                phoneHeight);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);

        View view = findViewById(R.id.phoneinput_root);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(bitmapDrawable);
        } else {
            view.setBackgroundDrawable(bitmapDrawable);
        }
    }

    private Context getContext() {
        return this;
    }

    private void phoneRegisterWork() {
        new AsyncTask<Void, Void, JSONObject>() {
            private String phonenumber;
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                EditText editText = (EditText) findViewById(R.id.editText);
                phonenumber = editText.getText().toString();
                if (phonenumber.isEmpty()) {
                    AlertToast.warning(getContext(), getString(R.string.warning_phone_input));
                    cancel(true);
                    return;
                }
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().registerPhoneNumber(phonenumber);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                clearProgressDialog.cancel();
                if (jsonObject == null) {
                    AlertToast.error(getContext(), getString(R.string.error_to_work));
                    return;
                }

                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), getString(R.string.success_phone_input));
                    finish();
                    return;
                }
                /* 추후에 처리 */
                String reason = jsonObject.get("reason").toString();
                AlertToast.error(getContext(), reason);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.phoneinput_cornfirm) {
            phoneRegisterWork();
        }
    }
}