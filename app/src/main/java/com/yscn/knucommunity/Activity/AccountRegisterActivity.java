package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
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

        /* set Toolbar */
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

        /* Set Default Text */
        String name = UserData.getInstance().getStudentName();
        String studentnumber = UserData.getInstance().getStudentNumber();
        ((TextView) findViewById(R.id.register_name)).setText(name);
        ((TextView) findViewById(R.id.register_studentnumber)).setText(studentnumber);

        /* Set Listener */
        findViewById(R.id.register_apply_button).setOnClickListener(this);
        findViewById(R.id.register_profile).setOnClickListener(this);

        /* Set Decoded Sample Bitmap Background */
        Bitmap bitmap = ApplicationUtil.getInstance().decodeSampledBitmap(
                getResources(),
                R.drawable.bg_register,
                ApplicationUtil.getInstance().getScreenWidth(),
                ApplicationUtil.getInstance().getScreenHeight()
        );
        View view = findViewById(R.id.accountregister_root);
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }

        /* if SDK is higher than kitkat set translate statusbar */
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
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
            String password = getIntent().getStringExtra("password");

            /* Check Register Account Value */
            if (isValidatingValue(studentNumber, password, nickname, studentName)) {
                registerWork(studentNumber, password, nickname, studentName);
            }
        } else if (id == R.id.register_profile) {
            getProfilePicture();
        }
    }

    /**
     * @param studentNumber School StudentNumber
     * @param nickname      Nickname
     * @param studentName   StudentName
     * @return if Validate Value return true
     */
    private boolean isValidatingValue(String studentNumber, String password, String nickname, String studentName) {
        if (studentNumber.isEmpty()) {
            AlertToast.warning(getContext(), getString(R.string.warning_input_studentnumber));
            return false;
        } else if (nickname.isEmpty()) {
            AlertToast.warning(getContext(), getString(R.string.warning_input_nickname));
            return false;
        } else if (studentName.isEmpty()) {
            AlertToast.warning(getContext(), getString(R.string.warning_input_name));
            return false;
        } else if (profileUri == null) {
            AlertToast.warning(getContext(), getString(R.string.warning_input_profileimage));
            return false;
        } else if (password.isEmpty()) {
            AlertToast.warning(getContext(), getString(R.string.warning_input_password));
            return false;
        } else if (nickname.length() > 10) {
            AlertToast.warning(getContext(), getString(R.string.warning_input_nickname_lenght));
            return false;
        } else {
            return true;
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


    private void registerWork(String studentnumber, String password, String nickname, String name) {
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
                    loginStatus = NetworkUtil.getInstance().RegisterAppServer(strings[0], strings[1], strings[2], strings[3], profileUri);
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
                    case BANNICKNAME:
                        showToastMessage(getString(R.string.text_ban_nickname));
                        break;
                }
                progressDialog.cancel();
            }
        }.execute(studentnumber, password, nickname, name);
    }

    private void showToastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
