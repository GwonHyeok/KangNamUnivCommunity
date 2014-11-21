package com.yscn.knucommunity.Ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Items.MeetingListItems;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MeetingListAdapter extends ArrayAdapter<MeetingListItems> {
    private int resource;

    public MeetingListAdapter(Context context, int resource, ArrayList<MeetingListItems> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        View view = convertView;
        AdapterHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            view = inflater.inflate(resource, parent, false);

            holder = new AdapterHolder();
            holder.title = (TextView) view.findViewById(R.id.meeting_title);
            holder.sumary = (TextView) view.findViewById(R.id.meeting_school);
            holder.reply = (TextView) view.findViewById(R.id.meeting_reply);
            holder.time = (TextView) view.findViewById(R.id.meeting_time);
            holder.people = (TextView) view.findViewById(R.id.meeting_people_num);
            view.setTag(holder);
        } else {
            holder = (AdapterHolder) view.getTag();
        }
        MeetingListItems object = getItem(position);
        holder.title.setText(object.getTitle());
        holder.sumary.setText(object.getSummary());
        holder.time.setText(object.getTime());
        holder.reply.setText("[" + object.getReplyCount() + "]");
        holder.people.setText(String.valueOf(object.getPeopleCount()));
        if (object.getType() == MeetingListItems.TYPE.BOY_GROUP) {
            holder.people.setBackgroundResource(R.drawable.bg_meeting_male);
        } else if (object.getType() == MeetingListItems.TYPE.GIRL_GROUP) {
            holder.people.setBackgroundResource(R.drawable.bg_meeting_female);
        } else if (object.getType() == MeetingListItems.TYPE.SUCCESS_GROUP) {
            holder.people.setBackgroundResource(R.drawable.bg_meeting_success);
        }
        return view;
    }

    private class AdapterHolder {
        TextView title, sumary, reply, time, people;
    }
}
