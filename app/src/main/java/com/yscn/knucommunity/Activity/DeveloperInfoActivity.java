package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

/**
 * Created by GwonHyeok on 15. 2. 19..
 */
public class DeveloperInfoActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_developerinfo);

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 19) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ApplicationUtil.getInstance().setTypeFace(findViewById(R.id.developer_info_root));

        ImageView hyeokImageView = (ImageView) findViewById(R.id.developer_info_hyeok_profile);
        ImageView mhwanImageView = (ImageView) findViewById(R.id.developer_info_mhwan_profile);
        ImageView wooranImageView = (ImageView) findViewById(R.id.developer_info_wooran_profile);

        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(
                NetworkUtil.getInstance().getDeveloperProfileThumbURL("hyeok"),
                hyeokImageView,
                ImageLoaderUtil.getInstance().getThumbProfileImageOptions()
        );

        ImageLoader.getInstance().displayImage(
                NetworkUtil.getInstance().getDeveloperProfileThumbURL("mhwan"),
                mhwanImageView,
                ImageLoaderUtil.getInstance().getThumbProfileImageOptions()
        );

        ImageLoader.getInstance().displayImage(
                NetworkUtil.getInstance().getDeveloperProfileThumbURL("wooran"),
                wooranImageView,
                ImageLoaderUtil.getInstance().getThumbProfileImageOptions()
        );
    }

    public void onClickLeeFB(View view) {
        openWebPage("http://www.facebook.com/mmimics");
    }

    public void onClickMhwanFB(View view) {
        openWebPage("http://www.facebook.com/mhwanbae21");
    }

    public void onClickHyeokFB(View view) {
        openWebPage("http://facebook.com/Hyeok.G");
    }

    public void openWebPage(String URL) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));
        startActivity(intent);
    }
}
