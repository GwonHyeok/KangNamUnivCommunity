package com.yscn.knucommunity.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 24..
 */
public class StudentCouncilWelfareDialog extends Dialog {
    private String mShopname, mWelfareinfo;

    public StudentCouncilWelfareDialog(Context context) {
        super(context);
        initDialog();
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_studentcouncilwelfaredialog);
        viewInit();
    }

    private void viewInit() {
        TextView shopNameView = (TextView) findViewById(R.id.welfaredialog_shopname);
        TextView welfareInfoView = (TextView) findViewById(R.id.welfaredialog_welfareinfo);
        View okButtonView = findViewById(R.id.welfaredialog_ok);
        okButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        shopNameView.setText(mShopname);
        welfareInfoView.setText(mWelfareinfo);
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    public void setShopInfo(String shopname) {
        this.mShopname = shopname;
    }

    public void setWelfareinfo(String welfareinfo) {
        this.mWelfareinfo = welfareinfo;
    }
}
