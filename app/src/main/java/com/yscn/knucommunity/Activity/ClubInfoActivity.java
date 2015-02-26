package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class ClubInfoActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private ClubInfoAdapter mAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        viewInit();
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Toolbar toolbar = new Toolbar(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("중앙 동아리");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.clubinfo_list_primary_color));
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView listView = new ListView(this);
        mAdapter = new ClubInfoAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<ClubInfoItem>());

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setDivider(new ColorDrawable(0xFFBDBDBD));
        listView.setDividerHeight(1);
        linearLayout.addView(toolbar);
        linearLayout.addView(listView);

        setContentView(linearLayout);

        getClubInfo();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }


    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.clubinfo_list_primary_dark_color));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, ClubDetailActivity.class);
        intent.putExtra("clubid", mAdapter.mItems.get(i).getId());
        intent.putExtra("clubname", mAdapter.mItems.get(i).getName());
        startActivity(intent);
    }

    public void getClubInfo() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(ClubInfoActivity.this);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getSchoolClubInfo();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonobject) {
                dialog.cancel();
                if (jsonobject == null) {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                    return;
                }

                String result = jsonobject.get("result").toString();
                if (result.equals("success")) {
                    mAdapter.clearItems();
                    JSONArray jsonarray = (JSONArray) jsonobject.get("data");
                    for (Object object : jsonarray) {
                        JSONObject dataJsonObject = (JSONObject) object;
                        String name = dataJsonObject.get("name").toString();
                        String id = dataJsonObject.get("id").toString();
                        mAdapter.addItem(new ClubInfoItem(id, name));
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                }
            }
        }.execute();
    }

    private class ClubInfoItem {
        private String name, id;

        public ClubInfoItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }
    }

    private class ClubInfoAdapter extends ArrayAdapter<ArrayList<ClubInfoItem>> {
        int mResourceId;
        private ArrayList<ClubInfoItem> mItems;

        public ClubInfoAdapter(Context context, int resource, ArrayList<ClubInfoItem> items) {
            super(context, resource);
            this.mResourceId = resource;
            this.mItems = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            RiffleMapViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(this.getContext()).inflate(mResourceId, parent, false);
                viewHolder = new RiffleMapViewHolder();
                viewHolder.simpleTextview = (TextView) view.findViewById(android.R.id.text1);
                view.setTag(viewHolder);
                ApplicationUtil.getInstance().setTypeFace(view);
            } else {
                view = convertView;
                viewHolder = (RiffleMapViewHolder) view.getTag();
            }
            viewHolder.simpleTextview.setText(mItems.get(position).getName());
            return view;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        public void clearItems() {
            mItems.clear();
        }

        public void addItem(ClubInfoItem clubInfoItem) {
            mItems.add(clubInfoItem);
        }

        private class RiffleMapViewHolder {
            private TextView simpleTextview;
        }
    }
}
