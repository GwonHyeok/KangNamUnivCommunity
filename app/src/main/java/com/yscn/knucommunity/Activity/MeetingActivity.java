package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.MeetingListAdapter;
import com.yscn.knucommunity.Items.MeetingListItems;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class MeetingActivity extends MenuBaseActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_meeting);
        getSupportActionBar().hide();

        findViewById(R.id.meeting_write).setOnClickListener(this);
        findViewById(R.id.open_menu).setOnClickListener(this);

        ListView listView = (ListView) findViewById(R.id.meeting_list);
        ArrayList<MeetingListItems> list = new ArrayList<MeetingListItems>();
        list.add(new MeetingListItems(MeetingListItems.TYPE.BOY_GROUP, "남자 4명", "건국대 인미공", "2014.10.17", 10, 4));
        list.add(new MeetingListItems(MeetingListItems.TYPE.SUCCESS_GROUP, "여자 4명", "강남대 컴미공", "2014.10.17", 20, 4));
        list.add(new MeetingListItems(MeetingListItems.TYPE.GIRL_GROUP, "여자 4명", "강남대 사복", "2014.10.17", 1, 4));
        listView.setAdapter(new MeetingListAdapter(this, R.layout.ui_meetinglist, list));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.meeting_write) {
            startActivity(new Intent(this, MeetingWriteActivity.class));
        }
    }
}
