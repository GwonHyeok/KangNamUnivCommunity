package com.yscn.knucommunity.Activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ImageLoaderUtil;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by GwonHyeok on 15. 2. 18..
 */
public class ImageCollectionActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_imagecollection);

        final String[] imageUrls = getIntent().getStringArrayExtra("Imageurls");
        final int position = getIntent().getIntExtra("Position", 0);

        if (imageUrls == null) {
            AlertToast.error(this, R.string.error_to_work);
            finish();
            return;
        }

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return imageUrls.length;
            }

            @Override
            public Object instantiateItem(ViewGroup viewGroup, int position) {
                View view = LayoutInflater.from(ImageCollectionActivity.this)
                        .inflate(R.layout.ui_collection_image_view, viewGroup, false);
                PhotoView imageView = (PhotoView) view.findViewById(R.id.imageview);
                ImageLoaderUtil.getInstance().initImageLoader();
                ImageLoader.getInstance().displayImage(imageUrls[position], imageView);
                viewGroup.addView(view);
                return view;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.imagecollection_viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(position, false);
    }
}
