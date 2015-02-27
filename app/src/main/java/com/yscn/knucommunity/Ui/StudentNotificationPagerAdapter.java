package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.yscn.knucommunity.CustomView.DividerItemDecoration;
import com.yscn.knucommunity.Items.StudentNotificationItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 2. 3..
 */
public class StudentNotificationPagerAdapter extends FragmentPagerAdapter {
    private String[] mPagerTitle;

    public StudentNotificationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setPageTitle(String[] pagerTitle) {
        mPagerTitle = pagerTitle;
    }

    @Override
    public Fragment getItem(int position) {
        return MyNotificationFragment.newInstance(position);
    }

    @Override
    public String getPageTitle(int position) {
        return mPagerTitle[position];
    }

    @Override
    public int getCount() {
        return mPagerTitle.length;
    }

    public static class MyNotificationFragment extends Fragment {
        private int currentPage = 1;
        private int perPageSize = 15;
        private boolean isRefresh = false;
        private StudentNotificationItemAdapter mNotificationItemAdapter;
        private int mPosition;
        private Context mContext;
        private RecyclerView mRecyclerview;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private LinearLayoutManager mLayoutManager;
        private ProgressBar mProgressBar;

        static MyNotificationFragment newInstance(int position) {
            MyNotificationFragment f = new MyNotificationFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            f.setArguments(bundle);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPosition = getArguments().getInt("position", -1);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_mynotification_list, container, false);
            viewInit(view);
            return view;
        }

        private void viewInit(View view) {
            mContext = view.getContext();
            mNotificationItemAdapter = new StudentNotificationItemAdapter();
            mLayoutManager = new LinearLayoutManager(mContext);

            mProgressBar = (ProgressBar) view.findViewById(R.id.mynotifi_progressbar);

            mRecyclerview = (RecyclerView) view.findViewById(R.id.mynotifi_list_recyclerview);
            mRecyclerview.setLayoutManager(mLayoutManager);
            mRecyclerview.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
            mRecyclerview.setAdapter(mNotificationItemAdapter);

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mynotifi_list_swiperefreshlayout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    /* 새로고침일 경우 페이지를 현재위치로 하고 데이터를 전부 날린다 */
                    currentPage = 1;
                    mNotificationItemAdapter.getItemses().clear();
                    getMoreData();
                }
            });

            mRecyclerview.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        int itemSize = mLayoutManager.getItemCount();
                        if ((currentPage - 1) * perPageSize == itemSize && !isRefresh) {
                            getMoreData();
                        }
                    }
                }
            });
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getMoreData();
        }

        public void getMoreData() {
            new AsyncTask<Void, Void, JSONObject>() {

                @Override
                protected void onPreExecute() {
                    isRefresh = true;
                    if (!mSwipeRefreshLayout.isRefreshing()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    try {
                        if (mPosition == 0) {
                            return NetworkUtil.getInstance().checkIsLoginUser().getMyNotify(currentPage);
                        } else if (mPosition == 1) {
                            return NetworkUtil.getInstance().checkIsLoginUser().getMyBoardList(currentPage);
                        } else if (mPosition == 2) {
                            return NetworkUtil.getInstance().checkIsLoginUser().getMyCommentList(currentPage);
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    currentPage = currentPage + 1;
                    if (jsonObject == null) {
                        AlertToast.error(mContext, R.string.error_to_work);
                        return;
                    }
                    Log.d(getClass().getSimpleName(), jsonObject.toJSONString());
                    JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                    ArrayList<StudentNotificationItems> itemses = mNotificationItemAdapter.getItemses();

                    if (mPosition == 0) {
                        for (Object obj : jsonArray) {
                            JSONObject dataObject = (JSONObject) obj;
                            String contentTitle = dataObject.get("title").toString();
                            String boardid = dataObject.get("boardid").toString();
                            String time = getSimpleDetailTime(dataObject.get("time").toString());
                            String nickname = dataObject.get("nickname").toString();
                            String contentid = dataObject.get("id").toString();
                            String writer = dataObject.get("writer").toString();

                            String new_title;
                            if (!contentTitle.isEmpty()) {
                                new_title = mContext.getString(R.string.text_studentinfo_notify_noti_reply_base);
                                new_title = String.format(new_title, nickname, getBoardName(boardid), contentTitle);
                            } else {
                                new_title = mContext.getString(R.string.text_studentinfo_notify_noti_reply_empty_titlebase);
                                new_title = String.format(new_title, nickname, getBoardName(boardid));
                            }
                            itemses.add(new StudentNotificationItems(StudentNotificationItems.Type.Notify, writer, new_title, boardid, contentid, time));
                        }
                    } else if (mPosition == 1 || mPosition == 2) {
                        for (Object obj : jsonArray) {
                            JSONObject dataObject = (JSONObject) obj;
                            String contentTitle = dataObject.get("title").toString();
                            String boardid = dataObject.get("boardid").toString();
                            String time = getSimpleDetailTime(dataObject.get("time").toString());
                            String contentid = dataObject.get("id").toString();
                            String newTitle = mContext.getString(R.string.text_studentinfo_notify_board_base);
                            newTitle = String.format(newTitle, getBoardName(boardid), contentTitle);
                            itemses.add(new StudentNotificationItems(StudentNotificationItems.Type.Myboard, null, newTitle, boardid, contentid, time));
                        }
                    }

                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mProgressBar.setVisibility(View.GONE);
                    isRefresh = false;
                    mNotificationItemAdapter.notifyDataSetChanged();
                }
            }.execute();
        }

        private String getBoardName(String boardid) {
            switch (boardid) {
                case "1":
                    return mContext.getString(R.string.community_freeboard_title);
                case "2":
                    return mContext.getString(R.string.community_faq_title);
                case "3":
                    return mContext.getString(R.string.community_greenlight_title);
                case "4":
                    return mContext.getString(R.string.community_meeting_title);
                case "5":
                    return mContext.getString(R.string.taxi_share_title);
                case "6":
                    return mContext.getString(R.string.community_market_title);
            }
            return "";
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
    }
}
