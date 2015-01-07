package com.yscn.knucommunity.Activity;

import android.os.Bundle;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
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

        ((TextView) findViewById(R.id.studentinfo_nickname)).setText(UserData.getInstance().getStudentName());
        ((TextView) findViewById(R.id.studentinfo_name)).setText(UserData.getInstance().getStudentName());
        ((TextView) findViewById(R.id.studentinfo_studentnumber)).setText(UserData.getInstance().getStudentNumber());

        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(
                UrlList.PROFILE_IMAGE_URL + UserData.getInstance().getStudentNumber(),
                (CircleImageView) findViewById(R.id.studentinfo_profile_image),
                ImageLoaderUtil.getInstance().getDefaultOptions());
    }
}
