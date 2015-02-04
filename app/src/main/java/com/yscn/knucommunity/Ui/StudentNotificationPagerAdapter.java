package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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
public class StudentNotificationPagerAdapter extends PagerAdapter {
    private String[] mPagerTitle;
    private Context mContext;
    private int visibleItemCount, pastVisiblesItems, totalItemCount;
    private int mNotifyPage, mBoardPage;
    private int perPageSize = 15;
    private boolean isRefresh = true;

    public StudentNotificationPagerAdapter(Context context) {
        this.mContext = context;
    }

    public void setPageTitle(String[] pagerTitle) {
        mPagerTitle = pagerTitle;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        Log.d(getClass().getSimpleName(), "instatiateItem : " + position);
        View view;
        if (position == 0) {
            view = getMyNotificationView();
        } else {
            view = getMyBoardView();
        }
        viewGroup.addView(view);
        return view;
    }

    public void getMoreData(final SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView,
                            final StudentNotificationItemAdapter adapter, final boolean forceLoad) {
        new AsyncTask<Void, Void, JSONObject>() {
            private int mPosition = -1;

            @Override
            protected void onPreExecute() {
                int currentPage = -1;

                if (recyclerView.getTag().toString().equals("notifi")) {
                    mPosition = 0;
                } else if (recyclerView.getTag().toString().equals("board")) {
                    mPosition = 1;
                }

                if (forceLoad) {
                    adapter.getItemses().clear();
                    mBoardPage = 1;
                    mNotifyPage = 1;
                }


                if (mPosition == 0) {
                    Log.d(getClass().getSimpleName(), "Current Position Is Notify");
                    currentPage = mNotifyPage;
                } else if (mPosition == 1) {
                    Log.d(getClass().getSimpleName(), "Current Position Is Board");
                    currentPage = mBoardPage;
                }

                int childSize = adapter.getItemCount();

                if (forceLoad) {
                    Log.d(getClass().getSimpleName(), "Force Load");
                } else if ((currentPage - 1) * perPageSize == childSize) {
                    Log.d(getClass().getSimpleName(), "DefaultDate Load");
                } else {
                    Log.d(getClass().getSimpleName(), "Cancle");
                    cancel(true);
                }
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {

                    if (mPosition == 0) {
                        return NetworkUtil.getInstance().getMyNotify(mNotifyPage);
                    } else if (mPosition == 1) {
                        return NetworkUtil.getInstance().getMyBoardList(mBoardPage);
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (mPosition == 0) {
                    mNotifyPage = mNotifyPage + 1;
                } else if (mPosition == 1) {
                    mBoardPage = mBoardPage + 1;
                }
                if (jsonObject == null) {
                    AlertToast.error(mContext, R.string.error_to_work);
                    return;
                }
                Log.d(getClass().getSimpleName(), jsonObject.toJSONString());
                JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                ArrayList<StudentNotificationItems> itemses = adapter.getItemses();

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
                } else if (mPosition == 1) {
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

                resetRefresh();
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
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

    private ViewGroup getMyNotificationView() {
        final SwipeRefreshLayout rootView = (SwipeRefreshLayout) getBaseView();

        /* SwipeRefreshLayout child(0) == ImageView */
        final RecyclerView containerView = (RecyclerView) rootView.getChildAt(1);
        final StudentNotificationItemAdapter notificationItemAdapter = new StudentNotificationItemAdapter();
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        containerView.setLayoutManager(mLayoutManager);
        containerView.setAdapter(notificationItemAdapter);
        containerView.setTag("notifi");
        getMoreData(rootView, containerView, notificationItemAdapter, true);
        rootView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMoreData(rootView, containerView, notificationItemAdapter, true);
            }
        });
        containerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isRefresh) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isRefresh = false;
                        getMoreData(rootView, recyclerView, notificationItemAdapter, false);
                        /* Refresh */
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return rootView;
    }

    private ViewGroup getMyBoardView() {
        final SwipeRefreshLayout rootView = (SwipeRefreshLayout) getBaseView();

        /* SwipeRefreshLayout child(0) == ImageView */
        final RecyclerView containerView = (RecyclerView) rootView.getChildAt(1);
        final StudentNotificationItemAdapter notificationItemAdapter = new StudentNotificationItemAdapter();
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        containerView.setLayoutManager(mLayoutManager);
        containerView.setAdapter(notificationItemAdapter);
        containerView.setTag("board");
        getMoreData(rootView, containerView, notificationItemAdapter, true);
        rootView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMoreData(rootView, containerView, notificationItemAdapter, true);
            }
        });
        containerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isRefresh) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isRefresh = false;
                        getMoreData(rootView, recyclerView, notificationItemAdapter, false);
                        /* Refresh */
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return rootView;
    }

    private ViewGroup getBaseView() {
        SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(mContext);
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        swipeRefreshLayout.addView(recyclerView);
        return swipeRefreshLayout;
    }

    public void resetRefresh() {
        this.isRefresh = true;
    }

    @Override
    public String getPageTitle(int position) {
        return mPagerTitle[position];
    }

    @Override
    public int getCount() {
        return mPagerTitle.length;
    }

    @Override
    public void destroyItem(ViewGroup pager, int position, Object view) {
        pager.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
