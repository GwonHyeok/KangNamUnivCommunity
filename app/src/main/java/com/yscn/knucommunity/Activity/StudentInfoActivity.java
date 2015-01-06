package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class StudentInfoActivity extends MenuBaseActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_studentinfo);

        Window window = getWindow();

// Enable status bar translucency (requires API 19)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// Disable status bar translucency (requires API 19)
        window.getAttributes().flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        ((TextView) findViewById(R.id.studentinfo_nickname)).setText(UserData.getInstance().getStudentName());
        ((TextView) findViewById(R.id.studentinfo_name)).setText(UserData.getInstance().getStudentName());
        ((TextView) findViewById(R.id.studentinfo_studentnumber)).setText(UserData.getInstance().getStudentNumber());

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        }

        ImageLoader.getInstance().displayImage(UrlList.PROFILE_IMAGE_URL + UserData.getInstance().getStudentNumber(),
                (CircleImageView) findViewById(R.id.studentinfo_profile_image));
    }

    private Context getContext() {
        return StudentInfoActivity.this;
    }

}
