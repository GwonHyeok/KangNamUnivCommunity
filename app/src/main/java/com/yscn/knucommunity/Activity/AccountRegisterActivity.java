package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 11. 26..
 */
public class AccountRegisterActivity extends ActionBarActivity implements View.OnClickListener {
    private int GET_PICTURE_RESULT_CODE = 0X10;
    private Uri profileUri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accoutregister);

        /* Remove Action Bar */
        getSupportActionBar().hide();

        /* ActionBar Init */
        View actionbar_view = findViewById(R.id.register_actionbar);
        TextView title_View = (TextView) actionbar_view.findViewById(R.id.actionbar_center_base_title);
        View button_view = actionbar_view.findViewById(R.id.actionbar_center_base_image);
        title_View.setText(getString(R.string.register_text));
        button_view.setOnClickListener(this);

        /* Set Default Text */
        String name = UserData.getInstance().getStudentName();
        String studentnumber = UserData.getInstance().getStudentNumber();
        ((TextView) findViewById(R.id.register_name)).setText(name);
        ((TextView) findViewById(R.id.register_studentnumber)).setText(studentnumber);

        /* Set Listener */
        findViewById(R.id.register_apply_button).setOnClickListener(this);
        findViewById(R.id.register_profile).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.register_apply_button) {
            String studentNumber = UserData.getInstance().getStudentNumber();
            String studentName = UserData.getInstance().getStudentName();
            String nickname = ((EditText) findViewById(R.id.register_nickname)).getText().toString();
            registerWork(studentNumber, nickname, studentName);
        } else if (id == R.id.register_profile) {
            getProfilePicture();
        } else if (id == R.id.actionbar_center_base_image) {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_RESULT_CODE && resultCode == RESULT_OK) {
            /* ImageLoader가 init 되어 있지 않으면 init */
            ImageLoaderUtil.getInstance().initImageLoader();

            /* 유저가 선택한 이미지를 ImageView에 적용 */
            ImageLoader.getInstance().displayImage(data.getData().toString(), (CircleImageView) findViewById(R.id.register_profile));

            profileUri = data.getData();
        }
    }

    private Context getContext() {
        return this;
    }

    private void getProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_PICTURE_RESULT_CODE);
    }


    private void registerWork(String studentnumber, String nickname, String name) {
        new AsyncTask<String, Void, NetworkUtil.LoginStatus>() {
            private ClearProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ClearProgressDialog(getContext());
                progressDialog.show();
            }

            @Override
            protected NetworkUtil.LoginStatus doInBackground(String... strings) {
                NetworkUtil.LoginStatus loginStatus = NetworkUtil.LoginStatus.FAIL;
                try {
                    loginStatus = NetworkUtil.getInstance().RegisterAppServer(strings[0], strings[1], strings[2], profileUri);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
                return loginStatus;
            }

            @Override
            protected void onPostExecute(NetworkUtil.LoginStatus status) {
                switch (status) {
                    case SUCCESS:
                        showToastMessage(getString(R.string.register_success));
                        finish();
                        Intent intent = new Intent(getContext(), Splash.class);
                        startActivity(intent);
                        break;
                    case HASMEMBER:
                        showToastMessage(getString(R.string.register_already_exist));
                        break;
                    case FAIL:
                        showToastMessage(getString(R.string.register_fail));
                        break;
                }
                progressDialog.cancel();
            }
        }.execute(studentnumber, nickname, name);
    }

    private void showToastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
