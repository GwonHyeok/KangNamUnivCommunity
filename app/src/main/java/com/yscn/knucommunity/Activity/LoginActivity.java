package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
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
            Toast.makeText(this, "아이디나 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
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
                        Toast.makeText(getContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                    progressdialog.cancel();
                }
            }.execute(studentID, studentPW);
        }
    }
}
