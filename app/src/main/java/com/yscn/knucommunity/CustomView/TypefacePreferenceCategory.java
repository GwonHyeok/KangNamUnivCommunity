package com.yscn.knucommunity.CustomView;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 17..
 */
public class TypefacePreferenceCategory extends PreferenceCategory {

    public TypefacePreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TypefacePreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypefacePreferenceCategory(Context context) {
        super(context);
    }

    @Override
    public void onBindView(@NonNull View view) {
        super.onBindView(view);
        ApplicationUtil.getInstance().setTypeFace(view);
    }
}
