package com.yscn.knucommunity.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 1. 28..
 */
public class ShareTaxiWriteActivity extends ActionBarActivity implements View.OnClickListener {
    private TextView departureTime;
    private EditText departure, destination, peopleCount, content;
    private String defaultDateFormat = "yyyy.MM.DD hh:mm";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_sharetaxi_write);
        toolbarInit();
        viewInit();
    }

    private void viewInit() {
        findViewById(R.id.taxi_share_write_departure_time).setOnClickListener(this);
        findViewById(R.id.taxi_share_write_peoplecount).setOnClickListener(this);
        departureTime = (TextView) findViewById(R.id.taxi_share_write_departuretime_textview);
        departure = (EditText) findViewById(R.id.taxi_share_write_departure_textview);
        destination = (EditText) findViewById(R.id.taxi_share_write_destination_textview);
        peopleCount = (EditText) findViewById(R.id.taxi_share_write_peoplecount_textview);
        content = (EditText) findViewById(R.id.taxi_share_write_content_textview);
    }

    private void toolbarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);

        findViewById(R.id.board_write_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidateValue()) {
                    writeBoard();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void writeBoard() {
        new AsyncTask<Void, Void, JSONObject>() {
            ClearProgressDialog clearProgressDialog;
            String str_time;
            String str_departure;
            String str_destination;
            String str_peoplecount;
            String str_content;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(ShareTaxiWriteActivity.this);
                clearProgressDialog.show();

                str_time = reFormatTime(departureTime.getText());
                str_departure = departure.getText().toString();
                str_destination = destination.getText().toString();
                str_peoplecount = peopleCount.getText().toString();
                str_content = content.getText().toString();

                if (str_time == null) {
                    cancel(true);
                    AlertToast.error(ShareTaxiWriteActivity.this, getString(R.string.error_to_work));
                    clearProgressDialog.cancel();
                }
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().writeShareTaxiBoard(str_time, str_departure,
                            str_destination, str_peoplecount, str_content);
                } catch (IOException | org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                clearProgressDialog.cancel();
                if (jsonObject == null) {
                    AlertToast.error(ShareTaxiWriteActivity.this, getString(R.string.error_to_work));
                    return;
                }
                String result = jsonObject.get("result").toString();

                if (result.equals("success")) {
                    AlertToast.success(ShareTaxiWriteActivity.this, getString(R.string.success_board_write));
                    setResult(RESULT_OK);
                    finish();
                } else if (result.equals("fail")) {
                    String reason = jsonObject.get("reason").toString();
                    Log.d(getClass().getSimpleName(), reason);
                }
            }
        }.execute();
    }

    private String reFormatTime(CharSequence src_time) {
        SimpleDateFormat src_format = new SimpleDateFormat(defaultDateFormat);
        SimpleDateFormat dst_format = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
        try {
            Date src_date = src_format.parse(src_time.toString());
            return dst_format.format(src_date);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean isValidateValue() {
        if (departureTime.getText().toString().isEmpty()) {
            AlertToast.warning(this, getString(R.string.warning_taxi_share_write_time));
            return false;
        } else if (departure.getText().toString().isEmpty()) {
            AlertToast.warning(this, getString(R.string.warning_taxi_share_write_departure));
            return false;
        } else if (destination.getText().toString().isEmpty()) {
            AlertToast.warning(this, getString(R.string.warning_taxi_share_write_destination));
            return false;
        } else if (peopleCount.getText().toString().isEmpty()) {
            AlertToast.warning(this, getString(R.string.warning_taxi_share_write_peoplecount));
            return false;
        } else if (content.getText().toString().isEmpty()) {
            AlertToast.warning(this, getString(R.string.warning_taxi_share_write_content));
            return false;
        } else {
            return true;
        }

    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(null, mYear, mMonth, mDay, true);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, final int year, final int month, final int day) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(null, hour, minute, true, true);
                timePickerDialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                        TextView textView = (TextView) findViewById(R.id.taxi_share_write_departuretime_textview);
                        calendar.set(year, month, day, hour, minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultDateFormat);
                        textView.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                });
                timePickerDialog.show(getSupportFragmentManager(), "timepicker");
            }
        });
        datePickerDialog.show(getSupportFragmentManager(), "datepicker");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.taxi_share_write_departure_time) {
            showDateTimePicker();
        }
    }
}
