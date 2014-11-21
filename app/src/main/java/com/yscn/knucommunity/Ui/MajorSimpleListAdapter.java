package com.yscn.knucommunity.Ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yscn.knucommunity.Items.MajorSimpleListItems;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 19..
 */
public class MajorSimpleListAdapter extends ArrayAdapter<MajorSimpleListItems> {
    private int resource;

    public MajorSimpleListAdapter(Context context, int resource, ArrayList<MajorSimpleListItems> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
        View view = convertView;
        AdapterHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            view = inflater.inflate(resource, parent, false);
            holder = new AdapterHolder();
            holder.title = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(holder);
        } else {
            holder = (AdapterHolder) view.getTag();
        }

        MajorSimpleListItems object = getItem(position);
        String majorName = object.getMajorName();
        holder.title.setText(majorName);
        return view;
    }

    private class AdapterHolder {
        TextView title;
    }
}
