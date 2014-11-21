package com.yscn.knucommunity.Ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Items.FaqListItems;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FaqListAdapter extends ArrayAdapter<FaqListItems> {

    public FaqListAdapter(Context context, int resource, ArrayList<FaqListItems> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        View view = convertView;
        AdapterHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            view = inflater.inflate(R.layout.ui_faqlist, parent, false);

            holder = new AdapterHolder();
//            holder.title = (TextView) view.findViewById(R.id.ui_studentcouncil_title);
//            holder.sumary = (TextView) view.findViewById(R.id.ui_studentcouncil_summary);
            view.setTag(holder);
        } else {
            holder = (AdapterHolder) view.getTag();
        }
        FaqListItems object = getItem(position);
//        holder.title.setText(object.getTitle());
//        holder.sumary.setText(object.getSummary());
        return view;
    }

    private class AdapterHolder {
        TextView title, sumary;
    }
}
