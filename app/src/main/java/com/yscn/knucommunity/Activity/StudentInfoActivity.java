package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class StudentInfoActivity extends MenuBaseActivity implements View.OnClickListener {
    private int GET_PICTURE_RESULT_CODE = 0x10;
    private ClearProgressDialog clearProgressDialog;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_studentinfo);

        getSimpleProfile();

        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.studentinfo_profile_image);
        circleImageView.setOnClickListener(this);

        findViewById(R.id.studentinfo_nickname_root).setOnClickListener(this);

        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(
                UrlList.PROFILE_THUMB_IMAGE_URL + UserData.getInstance().getStudentNumber(),
                circleImageView,
                ImageLoaderUtil.getInstance().getDefaultOptions());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.studentinfo_profile_image) {
            showEditProfileChangeDialog();
        } else if (id == R.id.studentinfo_nickname_root) {
            showNicknameChangeDialog();
        }
    }

    private void showEditProfileChangeDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.warning_title)
                .setMessage(R.string.profileimage_change_text)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getProfilePicture();
                    }
                })
                .setNegativeButton(R.string.NO, null)
                .show();
    }

    private void showNicknameChangeDialog() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        final EditText editText = new EditText(getContext());
        editText.setHint(R.string.text_nickname);
        editText.setBackgroundColor(Color.TRANSPARENT);
        linearLayout.addView(editText);
        int padding = (int) ApplicationUtil.getInstance().dpToPx(20);
        linearLayout.setPadding(padding, 0, padding, 0);
        ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        editText.setLayoutParams(layoutParams);

        new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = editText.getText().toString();
                        if (!nickname.isEmpty()) {
                            nicknameChange(nickname);
                        } else {
                            AlertToast.warning(getContext(), R.string.warning_input_nickname);
                        }
                    }
                })
                .setTitle(R.string.warning_title)
                .setMessage(R.string.nickname_change_text)
                .setNegativeButton(R.string.NO, null)
                .setView(linearLayout)
                .show();
    }

    private void getSimpleProfile() {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(true);
                    return;
                }
                showProgressDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getSimpleProfile();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                cancelProgressDialog();

                if (jsonObject == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }

                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    ((TextView) findViewById(R.id.studentinfo_name)).setText(jsonObject.get("name").toString());
                    ((TextView) findViewById(R.id.studentinfo_nickname)).setText(jsonObject.get("nickname").toString());
                    ((TextView) findViewById(R.id.studentinfo_studentnumber)).setText(UserData.getInstance().getStudentNumber());
                    return;
                }

                if (result.equals("fail")) {
                    String reason = jsonObject.get("reason").toString();
                    if (reason.equals("emptyuserinfo")) {
                        AlertToast.error(getContext(), R.string.error_empty_studentnumber_info);
                        UserData.getInstance().logoutUser();
                    }
                }
            }
        }.execute();
    }


    private void nicknameChange(String nickname) {
        new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(true);
                    return;
                }
                showProgressDialog();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    return NetworkUtil.getInstance().changeNickName(params[0]);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                cancelProgressDialog();

                if (jsonObject == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }

                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), R.string.success_nickname_change);
                    getSimpleProfile();
                    return;
                }

                if (result.equals("fail")) {
                    String reason = jsonObject.get("reason").toString();
                    if (reason.equals("emptyuserinfo")) {
                        AlertToast.error(getContext(), R.string.error_empty_studentnumber_info);
                        UserData.getInstance().logoutUser();
                    }
                }
            }
        }.execute(nickname);
    }

    private void getProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_PICTURE_RESULT_CODE);
    }

    private void showProgressDialog() {
        if (clearProgressDialog == null) {
            clearProgressDialog = new ClearProgressDialog(getContext());
        }
        if (clearProgressDialog.isShowing()) {
            cancelProgressDialog();
        }
        clearProgressDialog.show();

    }

    private void cancelProgressDialog() {
        clearProgressDialog.cancel();
    }

    private void editProfilePicture(Uri uri) {
        new AsyncTask<Uri, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                showNicknameChangeDialog();
            }

            @Override
            protected Boolean doInBackground(Uri... params) {
                try {
                    return NetworkUtil.getInstance().editProfilePicture(params[0]);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean bool) {
                if (bool) {
                    ImageLoaderUtil.getInstance().initImageLoader();
                    ImageLoader.getInstance().clearMemoryCache();
                    ImageLoader.getInstance().displayImage(
                            UrlList.PROFILE_THUMB_IMAGE_URL + UserData.getInstance().getStudentNumber(),
                            (CircleImageView) findViewById(R.id.studentinfo_profile_image),
                            ImageLoaderUtil.getInstance().getDefaultOptions());
                }
                cancelProgressDialog();
            }
        }.execute(uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_RESULT_CODE && resultCode == RESULT_OK) {
            editProfilePicture(data.getData());
        }
    }
}
