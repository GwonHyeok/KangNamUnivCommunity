package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 1. 27..
 */
public class AlertToast {

    public static void success(Context context, String string) {
        LinearLayout rootLayout = getView(context, 0xFF039BE5);
        showToast(context, string, rootLayout);
    }

    public static void warning(Context context, String string) {
        LinearLayout rootLayout = getView(context, 0xFFF6BE1A);
        showToast(context, string, rootLayout);
    }

    public static void error(Context context, String string) {
        LinearLayout rootLayout = getView(context, 0xFFF44336);
        showToast(context, string, rootLayout);
    }

    private static void showToast(Context context, String string, View customView) {
        int padding = (int) ApplicationUtil.getInstance().dpToPx(8);
        Toast toast = new Toast(context);
        TextView textView = (TextView) ((ViewGroup) customView).getChildAt(0);
        textView.setText(string);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(padding, padding, padding, padding);
        toast.setView(customView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private static LinearLayout getView(Context context, int color) {
        LinearLayout linearLayout = new LinearLayout(context);
        TextView textView = new TextView(context);
        if (Build.VERSION.SDK_INT >= 16) {
            linearLayout.setBackground(getBackgroundDrawable(color));
        } else {
            linearLayout.setBackgroundDrawable(getBackgroundDrawable(color));
        }
        linearLayout.addView(textView);
        return linearLayout;
    }

    private static Drawable getBackgroundDrawable(int color) {
        float strokeWidth = ApplicationUtil.getInstance().dpToPx(1);
        float radius = ApplicationUtil.getInstance().dpToPx(0);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(radius);
        return gradientDrawable;
    }
}
