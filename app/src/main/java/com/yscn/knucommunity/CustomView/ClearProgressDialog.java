package com.yscn.knucommunity.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

/**
 * Created by GwonHyeok on 14. 11. 24..
 */
public class ClearProgressDialog extends Dialog {

    public ClearProgressDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        setCancelable(false);
        setContentView(progressBar);
    }
}
