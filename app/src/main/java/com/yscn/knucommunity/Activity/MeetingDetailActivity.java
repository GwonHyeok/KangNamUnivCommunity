package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardDetailActivity;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */

public class MeetingDetailActivity extends BaseBoardDetailActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_meetingdetail);
        super.onCreate(bundle);
        findViewById(R.id.meeting_detail_replayview).setOnClickListener(this);
        setContent();
        ApplicationUtil.getInstance().setTypeFace(findViewById(R.id.meeting_detail_root));
    }

    private void setContent() {
        new AsyncTask<Void, Void, Void>() {
            private ClearProgressDialog clearProgressDialog;
            JSONObject meetingDataObject;
            JSONObject boardContentObject;
            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String contentID = getIntent().getStringExtra("contentID");
                try {
                    meetingDataObject = NetworkUtil.getInstance().getMeetingData(contentID);
                    boardContentObject = NetworkUtil.getInstance().getDefaultboardContent(contentID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void value) {
                clearProgressDialog.cancel();
                if (boardContentObject != null && meetingDataObject != null) {

                    String peopleCount = meetingDataObject.get("studentcount").toString();
                    String school = meetingDataObject.get("schoolname").toString();
                    String major = meetingDataObject.get("majorname").toString();
                    String gender = meetingDataObject.get("gender").toString();
                    String time = boardContentObject.get("time").toString();
                    String studentname = boardContentObject.get("writername").toString();
                    String studentNumber = boardContentObject.get("studentnumber").toString();
                    String matchingresult = meetingDataObject.get("matchingresult").toString();

                    board_studenuNumber = studentNumber;
                    invalidateOptionsMenu();

                    ((TextView) findViewById(R.id.meeting_detail_name)).setText(studentname);
                    ((TextView) findViewById(R.id.meeting_detail_time)).setText(getSimpleDetailTime(time));
                    ((TextView) findViewById(R.id.meeting_detail_title)).setText(getDefaultMeetingTitle(gender, Integer.parseInt(peopleCount), school, major));

                    ImageView profileImageView = (ImageView) findViewById(R.id.meeting_detail_profile);
                    setProfileImage(profileImageView, studentNumber);
                    ((TextView) findViewById(R.id.meeting_detail_content)).setText(boardContentObject.get("content").toString());

                    TextView meetingButton = (TextView) findViewById(R.id.meeting_detail_button);
                    if (matchingresult.equals("0") && board_studenuNumber.equals(UserData.getInstance().getStudentNumber())) {
                        meetingButton.setVisibility(View.VISIBLE);
                        meetingButton.setText(R.string.text_meeting_success_notyet);
                        meetingButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                        .setTitle(R.string.warning_title)
                                        .setMessage(R.string.text_are_you_sure_meeting_success)
                                        .setNegativeButton(R.string.NO, null)
                                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                updateMeetingResult(1);
                                            }
                                        }).show();
                                ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
                            }
                        });
                    }

                    if (matchingresult.equals("1")) {
                        meetingButton.setVisibility(View.VISIBLE);
                        meetingButton.setBackgroundResource(R.drawable.bg_button_meeting_successed);
                        meetingButton.setTextColor(getResources().getColor(R.color.meeting_success));
                        meetingButton.setText(R.string.text_meeting_success);
                        meetingButton.setOnClickListener(null);
                    }
                }
            }
        }.execute();
    }

    private void updateMeetingResult(final int matchingResult) {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    String contentID = getIntent().getStringExtra("contentID");
                    return NetworkUtil.getInstance().updateMeetingResult(contentID, matchingResult);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                clearProgressDialog.cancel();
                if (jsonObject == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }
                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), R.string.success_update_meeting);
                    /* Refresh Data */
                    setContent();
                } else {
                    AlertToast.error(getContext(), R.string.error_to_work);
                }
            }
        }.execute();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        /* 미팅은 무조건 글을 수정 할 수 없다. */
        menu.getItem(1).setVisible(false);
        return true;
    }

    protected void setDefaultData() {
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.MEETING;
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.board_white_pirmary_dark_color);
    }

    private String getDefaultMeetingTitle(String gender, int peopleCount, String school, String major) {
        String title = "";
        if (gender.equals("male")) {
            title += getString(R.string.gender_male);
        } else {
            title += getString(R.string.gender_female);
        }
        title += " | ";
        title += String.format(getString(R.string.community_meeting_people_count), peopleCount);
        title += " | ";
        title += school;
        title += " | ";
        title += major;
        return title;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.meeting_detail_replayview) {
            Intent intent = new Intent(this, FreeBoardReplyActivity.class);
            intent.putExtra("contentID", getIntent().getStringExtra("contentID"));
            intent.putExtra("title", getIntent().getStringExtra("title"));
            startActivity(intent);
        }
    }
}
