package com.yscn.knucommunity.CustomView;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 17..
 */
public class TypefaceCheckBoxPreference extends CheckBoxPreference {

    public TypefaceCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TypefaceCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypefaceCheckBoxPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindView(@NonNull View view) {
        super.onBindView(view);
        ApplicationUtil.getInstance().setTypeFace(view);
    }
}
