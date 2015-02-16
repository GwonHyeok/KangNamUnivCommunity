package com.yscn.knucommunity.CustomView;

import android.content.Context;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 17..
 */
public class TypefacePreference extends Preference {

    public TypefacePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TypefacePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypefacePreference(Context context) {
        super(context);
    }

    @Override
    public void onBindView(@NonNull View view) {
        super.onBindView(view);
        ApplicationUtil.getInstance().setTypeFace(view);
    }
}
