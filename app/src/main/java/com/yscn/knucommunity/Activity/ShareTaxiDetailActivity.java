package com.yscn.knucommunity.Activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

/**
 * Created by GwonHyeok on 15. 1. 26..
 */
public class ShareTaxiDetailActivity extends BaseBoardDetailActivity implements View.OnClickListener {
    private boolean isFolded = true;

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_sharetaxidetail);
        super.onCreate(bundle);

        findViewById(R.id.share_taxi_detail_with_info_root_view).setOnClickListener(this);
    }

    @Override
    protected void setDefaultData() {

    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.SHARETAXT;
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.share_taxi_detail_with_info_root_view) {
            View locationView = findViewById(R.id.share_taxi_detail_locationview);
            final View rootView = v;
            final int screenHeight = ApplicationUtil.getInstance().getScreenHeight();

            if (isFolded) {
                Log.d(getClass().getSimpleName(), "Screen Height : " + screenHeight);
                Log.d(getClass().getSimpleName(), "LocationView Height : " + locationView.getHeight());
                Log.d(getClass().getSimpleName(), "LocationView Y : " + locationView.getY());

                ValueAnimator upWithInfoView = ValueAnimator.ofInt(
                        ((int) locationView.getY() + locationView.getHeight() + rootView.getHeight()));

                upWithInfoView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int y = (int) animation.getAnimatedValue();
                        rootView.setTranslationY((float) -y);
                    }
                });

                upWithInfoView.setDuration(400);
                upWithInfoView.start();

                findViewById(R.id.share_taxi_detail_scroll_info_view).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_writer_profile).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_button).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_content).setVisibility(View.GONE);
                findViewById(R.id.share_taxi_detail_with_folding_view).setVisibility(View.VISIBLE);

                ImageView foldingButton = (ImageView) findViewById(R.id.share_taxi_detail_folding_button);
                foldingButton.setImageResource(R.drawable.ic_unfold);
            } else {
                ValueAnimator upWithInfoView = ValueAnimator.ofInt(
                        ((int) locationView.getY() + locationView.getHeight() + rootView.getHeight()), 0);

                upWithInfoView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int y = (int) animation.getAnimatedValue();
                        rootView.setTranslationY((float) - y);
                    }
                });
                upWithInfoView.setDuration(400);
                upWithInfoView.start();

                findViewById(R.id.share_taxi_detail_scroll_info_view).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_writer_profile).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_button).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_content).setVisibility(View.VISIBLE);
                findViewById(R.id.share_taxi_detail_with_folding_view).setVisibility(View.GONE);
                ImageView foldingButton = (ImageView) findViewById(R.id.share_taxi_detail_folding_button);
                foldingButton.setImageResource(R.drawable.ic_fold);
            }

            isFolded = !isFolded;
        }
    }
}