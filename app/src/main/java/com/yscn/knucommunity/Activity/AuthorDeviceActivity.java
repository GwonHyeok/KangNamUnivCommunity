package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.DividerItemDecoration;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 2. 5..
 */
public class AuthorDeviceActivity extends ActionBarActivity {
    private RecyclerView.Adapter<ViewHolder> viewHolderAdapter;
    private ArrayList<AuthorDeviceItems> itemses = new ArrayList<>();
    private ClearProgressDialog clearProgressDialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        LinearLayout rootLayout = new LinearLayout(this);
        Toolbar toolbar = new Toolbar(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setBackgroundColor(0xFF5321A8);
        toolbar.setTitle(R.string.text_setting_author_devices);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        rootLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = new RecyclerView(this);
        viewHolderAdapter = new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.ui_authordevices_card, viewGroup, false);
                v.setBackgroundResource(R.drawable.bg_default_select_item_effect);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, int i) {
                final int itemindex = i;
                viewHolder.getUserAgentView().setText(itemses.get(i).getLogin_useragent());
                viewHolder.getUserIpView().setText(itemses.get(i).getLogin_ip());
                viewHolder.getUserTimeView().setText(itemses.get(i).getLogin_time());
                viewHolder.getRootView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle(R.string.warning_title)
                                .setMessage(R.string.text_setting_author_device_logout)
                                .setNegativeButton(R.string.NO, null)
                                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doLogoutWithSession(itemses.get(itemindex).getLogin_session());
                                    }
                                }).create();
                        alertDialog.show();
                        ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
                    }
                });
                ApplicationUtil.getInstance().setTypeFace(viewHolder.getRootView());
            }

            @Override
            public int getItemCount() {
                return itemses.size();
            }
        };
        recyclerView.setAdapter(viewHolderAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        float paddingLeft = ApplicationUtil.getInstance().dpToPx(16);
        float height = ApplicationUtil.getInstance().dpToPx(32);

        TextView titleView = new TextView(getContext());
        titleView.setText(R.string.text_setting_author_devices_message);
        titleView.setTextColor(0xFF757575);
        titleView.setBackgroundColor(0xFFE9E9E9);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        titleView.setPadding((int) paddingLeft, 0, 0, 0);

        rootLayout.addView(toolbar);
        rootLayout.addView(titleView);
        rootLayout.addView(recyclerView);

        ViewGroup.LayoutParams layoutParams = titleView.getLayoutParams();
        layoutParams.height = (int) height;
        titleView.setLayoutParams(layoutParams);

        setContentView(rootLayout);
        showAuthorDevices();

        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void doLogoutWithSession(String login_session) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    return NetworkUtil.getInstance().doLogoutWithSession(params[0]);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject value) {
                cancelProgressDialog();
                if (value == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }
                showAuthorDevices();
            }
        }.execute(login_session);
    }

    private void showAuthorDevices() {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().getAuthorDevicesList();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject value) {
                cancelProgressDialog();
                if (value == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }
                itemses.clear();
                JSONArray dataArray = (JSONArray) value.get("data");
                for (Object object : dataArray) {
                    JSONObject jsonObject = (JSONObject) object;
                    String loginIP = jsonObject.get("ip_address").toString();
                    String user_agent = jsonObject.get("user_agent").toString();
                    String session = jsonObject.get("session_id").toString();
                    String logintime = jsonObject.get("login_time").toString();
                    itemses.add(new AuthorDeviceItems(session, user_agent, loginIP, getSimpleDetailTime(logintime)));
                }
                viewHolderAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private Context getContext() {
        return this;
    }

    protected String getSimpleDetailTime(String defaulttime) {
        String dataTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String newDateTimeFormat = "yyyy.MM.dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataTimeFormat);
        SimpleDateFormat newDateFormat = new SimpleDateFormat(newDateTimeFormat);

        String time;
        try {
            Date date = simpleDateFormat.parse(defaulttime);
            time = newDateFormat.format(date);
        } catch (java.text.ParseException ignore) {
            time = defaulttime;
        }
        return time;
    }

    private void showProgressDialog() {
        if (clearProgressDialog == null) {
            clearProgressDialog = new ClearProgressDialog(getContext());
        }
        if (clearProgressDialog.isShowing()) {
            cancelProgressDialog();
        }
        clearProgressDialog.show();
    }

    private void cancelProgressDialog() {
        clearProgressDialog.cancel();
    }

    private class AuthorDeviceItems {
        private String login_session, login_useragent, login_ip, login_time;

        public AuthorDeviceItems(String login_session, String login_useragent, String login_ip, String login_time) {
            this.login_session = login_session;
            this.login_useragent = login_useragent;
            this.login_ip = login_ip;
            this.login_time = login_time;
        }

        public String getLogin_session() {
            return login_session;
        }

        public String getLogin_useragent() {
            return login_useragent;
        }

        public String getLogin_ip() {
            return login_ip;
        }

        public String getLogin_time() {
            return login_time;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userAgentView, userIpView, userTimeView;
        private View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.userAgentView = (TextView) itemView.findViewById(R.id.authordevice_useragent);
            this.userIpView = (TextView) itemView.findViewById(R.id.authordevice_ip);
            this.userTimeView = (TextView) itemView.findViewById(R.id.authordevice_time);
        }

        public TextView getUserAgentView() {
            return userAgentView;
        }

        public TextView getUserIpView() {
            return userIpView;
        }

        public TextView getUserTimeView() {
            return userTimeView;
        }

        public View getRootView() {
            return rootView;
        }
    }
}
