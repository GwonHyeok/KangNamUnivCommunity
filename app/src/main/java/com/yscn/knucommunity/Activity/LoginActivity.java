package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        findViewById(R.id.login_button).setOnClickListener(this);


        /* Set Decoded Sample Bitmap Background */
        Bitmap bitmap = ApplicationUtil.getInstance().decodeSampledBitmap(
                getResources(),
                R.drawable.bg_login,
                ApplicationUtil.getInstance().getScreenWidth(),
                ApplicationUtil.getInstance().getScreenHeight()
        );

        View view = findViewById(R.id.loginactivity_root);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }

        /* if SDK is higher than kitkat set translate statusbar */
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.login_button) {
            doLogin();
        }
    }

    private Context getContext() {
        return this;
    }

    private void doLogin() {
        String studentID = ((EditText) findViewById(R.id.login_id)).getText().toString();
        String studentPW = ((EditText) findViewById(R.id.login_pw)).getText().toString();
        if (studentID.isEmpty() || studentPW.isEmpty()) {
            AlertToast.warning(this, getString(R.string.warning_input_login_form));
        } else {
            new AsyncTask<String, Void, NetworkUtil.LoginStatus>() {
                private ClearProgressDialog progressdialog;

                @Override
                protected void onPreExecute() {
                    progressdialog = new ClearProgressDialog(getContext());
                    progressdialog.show();
                }

                @Override
                protected NetworkUtil.LoginStatus doInBackground(String... strings) {
                    try {
                        return NetworkUtil.getInstance().LoginAppServer(strings[0], strings[1]);
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return NetworkUtil.LoginStatus.FAIL;
                }

                @Override
                protected void onPostExecute(NetworkUtil.LoginStatus loginStatus) {
                    if (loginStatus == NetworkUtil.LoginStatus.NOMEMBER) {
                        /* 어플 디비에 계정이 존재하지 않으면 계정 생성 액티비티 실행 */
                        finish();
                        startActivity(new Intent(getContext(), AccountRegisterActivity.class));
                    } else if (loginStatus == NetworkUtil.LoginStatus.SUCCESS) {
                        /* 로그인 액티비티 종료 후 메인 액티비티 실행*/
                        finish();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    } else if (loginStatus == NetworkUtil.LoginStatus.FAIL) {
                        /* 로그인 실패 */
                        AlertToast.error(getContext(), getString(R.string.error_login));
                    }
                    progressdialog.cancel();
                }
            }.execute(studentID, studentPW);
        }
    }
}
