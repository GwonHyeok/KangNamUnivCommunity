package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yscn.knucommunity.Activity.StudentCouncilDetailActivity;
import com.yscn.knucommunity.CustomView.DividerItemDecoration;
import com.yscn.knucommunity.CustomView.StudentCouncilWelfareDialog;
import com.yscn.knucommunity.Items.StudentCouncilListItems;
import com.yscn.knucommunity.Items.StudentCouncilWelfareListItems;
import com.yscn.knucommunity.R;
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
 * Created by GwonHyeok on 14. 11. 3..
 */

public class StudentCouncilAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"제휴정보", "복지카드"};
    private Context mContext;

    public StudentCouncilAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return partnershipInfoFragment.newInstance();
        } else if (position == 1) {
            return welfareCardFragment.newInstance();
        } else {
            return null;
        }
    }

    public static class partnershipInfoFragment extends Fragment {
        private ParternerShipInfoAdapter mAdapter;
        private ProgressBar mProgressBar;
        private RecyclerView mRecyclerView;
        private SwipeRefreshLayout mSwipeRefreshLayout;

        static partnershipInfoFragment newInstance() {
            return new partnershipInfoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_beat_list, container, false);
            mAdapter = new ParternerShipInfoAdapter();
            mProgressBar = (ProgressBar) view.findViewById(R.id.beat_progressbar);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.beat_list_recyclerview);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.beat_list_swiperefreshlayout);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updatePartnerInfo();
                }
            });
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            updatePartnerInfo();
        }

        private void updatePartnerInfo() {
            new AsyncTask<Void, Void, JSONObject>() {

                @Override
                protected void onPreExecute() {
                    if (!mSwipeRefreshLayout.isRefreshing()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    try {
                        return NetworkUtil.getInstance().getCouncilInfo();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    if (!mSwipeRefreshLayout.isRefreshing()) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (jsonObject == null) {
                        AlertToast.error(getActivity(), R.string.error_to_work);
                        return;
                    }
                    String result = jsonObject.get("result").toString();
                    if (result.equals("success")) {
                        mAdapter.clearItems();
                        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                        for (Object object : jsonArray) {
                            JSONObject dataObject = (JSONObject) object;
//                            "id": "1",
//                                    "type": "riffle",
//                                    "title": "테스트",
//                                    "message": "테스트 메세지 입니다.",
//                                    "writer": "권혁",
//                                    "time": "2014-11-19 00:59:05"
                            String id = dataObject.get("id").toString();
                            String title = dataObject.get("title").toString();
                            String message = dataObject.get("time").toString();
                            mAdapter.addItem(new StudentCouncilListItems(id, title, message));
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }.execute();
        }
    }

    public static class welfareCardFragment extends Fragment {
        private WelfareInfoAdapter mAdapter;
        private ProgressBar mProgressBar;
        private RecyclerView mRecyclerView;
        private SwipeRefreshLayout mSwipeRefreshLayout;

        static welfareCardFragment newInstance() {
            return new welfareCardFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_beat_list, container, false);
            mAdapter = new WelfareInfoAdapter();
            mProgressBar = (ProgressBar) view.findViewById(R.id.beat_progressbar);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.beat_list_recyclerview);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.beat_list_swiperefreshlayout);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updatePartnerInfo();
                }
            });
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            updatePartnerInfo();
        }

        private void updatePartnerInfo() {
            new AsyncTask<Void, Void, JSONObject>() {

                @Override
                protected void onPreExecute() {
                    if (!mSwipeRefreshLayout.isRefreshing()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    try {
                        return NetworkUtil.getInstance().getCouncilWelfareInfo();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    if (!mSwipeRefreshLayout.isRefreshing()) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (jsonObject == null) {
                        AlertToast.error(getActivity(), R.string.error_to_work);
                        return;
                    }
                    String result = jsonObject.get("result").toString();
                    if (result.equals("success")) {
                        mAdapter.clearItems();
                        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                        for (Object object : jsonArray) {
                            JSONObject dataObject = (JSONObject) object;
//                            "id": "1",
//                                    "shopname": "동촌동 샤브샤브 칼국수",
//                                    "welfareinfo": "6인 이상 테이블당 or 정량 주문시 음료 1병",
//                                    "geo": null
                            String title = dataObject.get("shopname").toString();
                            String message = dataObject.get("welfareinfo").toString();
                            mAdapter.addItem(new StudentCouncilWelfareListItems(title, message));
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }.execute();
        }
    }

    private static class WelfareInfoAdapter extends RecyclerView.Adapter<WelfareInfoViewHolder> {

        private ArrayList<StudentCouncilWelfareListItems> itemses = new ArrayList<>();

        @Override
        public WelfareInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_beat_list_card, parent, false);
            return new WelfareInfoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WelfareInfoViewHolder holder, final int position) {
            holder.titleView.setText(itemses.get(position).getTitle());
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StudentCouncilWelfareDialog dialog = new StudentCouncilWelfareDialog(v.getContext());
                    dialog.setShopInfo(itemses.get(position).getTitle());
                    dialog.setWelfareinfo(itemses.get(position).getSummary());
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemses.size();
        }

        public void clearItems() {
            itemses.clear();
        }

        public void addItem(StudentCouncilWelfareListItems item) {
            itemses.add(item);
        }

    }

    private static class WelfareInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView, timeView;
        private View rootView;

        public WelfareInfoViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ApplicationUtil.getInstance().setTypeFace(itemView);
            itemView.setBackgroundResource(R.drawable.bg_default_select_item_effect);
            titleView = (TextView) itemView.findViewById(R.id.beat_list_title);
            timeView = (TextView) itemView.findViewById(R.id.beat_list_time);
            timeView.setVisibility(View.GONE);
        }
    }

    private static class ParternerShipInfoAdapter extends RecyclerView.Adapter<ParternerShipInfoViewHolder> {

        private ArrayList<StudentCouncilListItems> itemses = new ArrayList<>();

        @Override
        public ParternerShipInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_beat_list_card, parent, false);
            return new ParternerShipInfoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ParternerShipInfoViewHolder holder, final int position) {
            holder.titleView.setText(itemses.get(position).getTitle());
            holder.timeView.setText(getSimpleDetailTime(itemses.get(position).getSummary()));
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), StudentCouncilDetailActivity.class);
                    intent.putExtra("contentid", itemses.get(position).getId());
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemses.size();
        }

        public void clearItems() {
            itemses.clear();
        }

        public void addItem(StudentCouncilListItems item) {
            itemses.add(item);
        }

        public String getSimpleDetailTime(String defaulttime) {
            String dataTimeFormat = "yyyy-MM-dd HH:mm:ss";
            String newDateTimeFormat = "yyyy.MM.dd";
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
    }

    private static class ParternerShipInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView, timeView;
        private View rootView;

        public ParternerShipInfoViewHolder(View itemView) {
            super(itemView);
            ApplicationUtil.getInstance().setTypeFace(itemView);
            itemView.setBackgroundResource(R.drawable.bg_default_select_item_effect);
            titleView = (TextView) itemView.findViewById(R.id.beat_list_title);
            timeView = (TextView) itemView.findViewById(R.id.beat_list_time);
            rootView = itemView;
        }
    }
}