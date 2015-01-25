package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class StudentInfoActivity extends MenuBaseActivity implements View.OnClickListener {
    private int GET_PICTURE_RESULT_CODE = 0x10;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_studentinfo);

        ((TextView) findViewById(R.id.studentinfo_nickname)).setText(UserData.getInstance().getStudentName());
        ((TextView) findViewById(R.id.studentinfo_name)).setText(UserData.getInstance().getStudentName());
        ((TextView) findViewById(R.id.studentinfo_studentnumber)).setText(UserData.getInstance().getStudentNumber());

        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.studentinfo_profile_image);
        circleImageView.setOnClickListener(this);

        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(
                UrlList.PROFILE_THUMB_IMAGE_URL + UserData.getInstance().getStudentNumber(),
                circleImageView,
                ImageLoaderUtil.getInstance().getDefaultOptions());
    }

    @Override
    public void onClick(View v) {
        getProfilePicture();
    }

    private void getProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_PICTURE_RESULT_CODE);
    }

    private void editProfilePicture(Uri uri) {
        new AsyncTask<Uri, Void, Boolean>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
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
                clearProgressDialog.cancel();
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
