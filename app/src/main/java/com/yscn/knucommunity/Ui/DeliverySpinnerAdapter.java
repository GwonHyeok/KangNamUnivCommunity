package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 1. 6..
 */
public class DeliverySpinnerAdapter extends ArrayAdapter<String> {
    private int currentPosition = 0;

    public DeliverySpinnerAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ApplicationUtil.getInstance().setTypeFace(view);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            ApplicationUtil.getInstance().setTypeFace(view);
        } else {
            view = convertView;
        }

        TextView text = (TextView) view;
        String item = getItem(position);
        text.setText(item);

        Resources r = getContext().getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, r.getDisplayMetrics());

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) px;
        text.setLayoutParams(layoutParams);

        if (position == this.currentPosition) {
            text.setTextColor(getContext().getResources().getColor(R.color.delivery_main_color));
        } else {
            text.setTextColor(Color.GRAY);
        }
        return view;
    }
}
