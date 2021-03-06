package com.yscn.knucommunity.Util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yscn.knucommunity.R;

/**
 * Created by GwonHyeok on 15. 1. 8..
 */
public class ImageLoaderUtil {
    private static ImageLoaderUtil instance;

    private ImageLoaderUtil() {

    }

    public static ImageLoaderUtil getInstance() {
        if (instance == null) {
            instance = new ImageLoaderUtil();
        }
        return instance;
    }

    public void initImageLoader() {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ApplicationContextProvider.getContext()));
        }
    }

    public DisplayImageOptions getDefaultOptions() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
    }

    public DisplayImageOptions getThumbProfileImageOptions() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnFail(R.drawable.ic_profile)
                .showImageOnLoading(R.drawable.ic_profile)
                .build();
    }

    public DisplayImageOptions getDiskCacheImageOptions() {
        return new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(400))
                .showImageForEmptyUri(R.drawable.bg_load_image_error)
                .showImageOnFail(R.drawable.bg_load_image_error)
                .build();
    }
}