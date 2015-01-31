package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.CustomView.NotifyFooterScrollView;
import com.yscn.knucommunity.Items.MeetingListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public class MeetingActivity extends MenuBaseActivity implements View.OnClickListener {
    private final int BOARD_WRITE_RESPONSE = 0X01;
    private int pageIndex = 1;
    private NotifyFooterScrollView scrollView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_meeting);
        toolbarInit();
        viewInit();
        getMeetingList();
    }

    private void viewInit() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.board_list_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadViewData();
            }
        });
        scrollView = (NotifyFooterScrollView) findViewById(R.id.meeting_list);
        scrollView.setonScrollToBottomListener(new NotifyFooterScrollView.onScrollToBottomListener() {
            @Override
            public void scrollToBottom() {
                View view = scrollView.getChildAt(0);
                if (view instanceof LinearLayout) {
                    int childSize = ((LinearLayout) view).getChildCount();
                    if (childSize == pageIndex * 15) {
                        pageIndex = (pageIndex + 1);
                        getMeetingList();
                    }
                }
            }
        });
    }

    private void getMeetingList() {
        new AsyncTask<Void, Void, ArrayList<MeetingListItems>>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(true);
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                clearProgressDialog = new ClearProgressDialog(getContext());
                if (!swipeRefreshLayout.isRefreshing()) {
                    clearProgressDialog.show();
                }
            }

            @Override
            protected ArrayList<MeetingListItems> doInBackground(Void... params) {
                ArrayList<MeetingListItems> itemses = null;
                try {
                    itemses = NetworkUtil.getInstance().getMeetingBoardList(pageIndex);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return itemses;
            }

            @Override
            protected void onPostExecute(ArrayList<MeetingListItems> itemses) {
                if (itemses == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                } else {
                    addScrollViewData(itemses);
                }
                swipeRefreshLayout.setRefreshing(false);
                clearProgressDialog.cancel();
            }
        }.execute();
    }

    private void addScrollViewData(ArrayList<MeetingListItems> itemses) {
        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);

        for (final MeetingListItems listItems : itemses) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.ui_meetinglist, linearLayout, false);
            TextView peopleCountView = (TextView) view.findViewById(R.id.meeting_people_num);
            TextView titleView = (TextView) view.findViewById(R.id.meeting_title);
            TextView schoolInfoView = (TextView) view.findViewById(R.id.meeting_school);
            TextView meetingTimeView = (TextView) view.findViewById(R.id.meeting_time);
            TextView replyCountView = (TextView) view.findViewById(R.id.meeting_reply);

            String title;
            int peopleCount = listItems.getPeopleCount();
            int replyCount = listItems.getReplyCount();
            MeetingListItems.TYPE type = listItems.getType();
            String gender = listItems.getGender();
            String majorType = listItems.getMajorname();
            String schoolName = listItems.getSchoolname();
            String time = listItems.getTime();

            if (gender.equals("male")) {
                title = String.format(getString(R.string.community_meeting_male_count), peopleCount);
                peopleCountView.setBackgroundResource(R.drawable.bg_meeting_male);
            } else {
                title = String.format(getString(R.string.community_meeting_female_count), peopleCount);
                peopleCountView.setBackgroundResource(R.drawable.bg_meeting_female);
            }

            if (type == MeetingListItems.TYPE.SUCCESS_GROUP) {
                peopleCountView.setBackgroundResource(R.drawable.bg_meeting_success);
            }

            peopleCountView.setText(String.valueOf(peopleCount));
            titleView.setText(title);
            schoolInfoView.setText(schoolName + " " + majorType);
            meetingTimeView.setText(getSimpleListTime(time));
            replyCountView.setText(String.valueOf(replyCount));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), MeetingDetailActivity.class);
                    intent.putExtra("people", listItems.getPeopleCount());
                    intent.putExtra("school", listItems.getSchoolname());
                    intent.putExtra("major", listItems.getMajorname());
                    intent.putExtra("gender", listItems.getGender());
                    intent.putExtra("time", listItems.getTime());
                    intent.putExtra("studentname", listItems.getStudentname());
                    intent.putExtra("writerStudentNumber", listItems.getWriter());
                    intent.putExtra("contentID", String.valueOf(listItems.getContentid()));
                    startActivity(intent);
                }
            });
            view.setBackgroundResource(R.drawable.bg_default_select_item_effect);
            linearLayout.addView(view);
        }
    }

    private void toolbarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_nav_menu_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
    }

    private void removeAllViewData() {
        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        linearLayout.removeAllViews();
    }

    private void reloadViewData() {
        pageIndex = 1;
        removeAllViewData();
        getMeetingList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BOARD_WRITE_RESPONSE && resultCode == RESULT_OK) {
            reloadViewData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_write:
                Intent intent = new Intent(getContext(), MeetingWriteActivity.class);
                startActivityForResult(intent, BOARD_WRITE_RESPONSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param deftime db상의 기본 DateTime 폼
     * @return 기본 보드에서 사용하는 시간폼으로 변경하여 리턴
     */
    protected String getSimpleListTime(String deftime) {
        String dataTimeFormat = "yyyy-MM-dd hh:mm:ss";
        String newDateTimeFormat = "yyyy.MM.dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataTimeFormat);
        SimpleDateFormat newDateFormat = new SimpleDateFormat(newDateTimeFormat);

        String time;
        try {
            Date date = simpleDateFormat.parse(deftime);
            time = newDateFormat.format(date);
        } catch (java.text.ParseException ignore) {
            // Date Parse Exception
            time = deftime;
        }
        return time;
    }

    @Override
    public void onClick(View view) {
    }
}