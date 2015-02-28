package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yscn.knucommunity.Activity.BeatDetailActivity;
import com.yscn.knucommunity.Activity.ImageCollectionActivity;
import com.yscn.knucommunity.CustomView.DividerItemDecoration;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 2. 17..
 */
public class BeatViewPagetAdapter extends FragmentPagerAdapter {
    private String[] mTabTitle;

    public BeatViewPagetAdapter(FragmentManager fm, Context context) {
        super(fm);
        mTabTitle = context.getResources().getStringArray(R.array.beat_tab_title);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == BEAT.CULTURE.getIndex()) {
            return CultureFragment.newInstance(BEAT.CULTURE);
        } else if (position == BEAT.WELFARE.getIndex()) {
            return CultureFragment.newInstance(BEAT.WELFARE);
        } else if (position == BEAT.REVIEW.getIndex()) {
            return CultureFragment.newInstance(BEAT.REVIEW);
        } else if (position == BEAT.QNA.getIndex()) {
            return CultureFragment.newInstance(BEAT.QNA);
        } else if (position == BEAT.LOOKNLOOK.getIndex()) {
            return LooknlookFragment.newInstance();
        } else if (position == BEAT.ETC.getIndex()) {
            return CultureFragment.newInstance(BEAT.ETC);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mTabTitle.length;
    }

    @Override
    public String getPageTitle(int position) {
        return mTabTitle[position];
    }

    public static enum BEAT {
        CULTURE(0), WELFARE(1), REVIEW(2), QNA(3), LOOKNLOOK(4), ETC(5);
        private int mIndex;

        BEAT(int index) {
            this.mIndex = index;
        }

        public int getIndex() {
            return this.mIndex;
        }
    }

    /* 문화, 복지, 기타 프래그먼트 */
    public static class CultureFragment extends Fragment {
        private RecyclerView mRecyclerView;
        private ProgressBar mProgressBar;
        private CultureAdapter mCultureAdapter;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private int mBeatIndex;

        static CultureFragment newInstance(BEAT beat) {
            CultureFragment f = new CultureFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", beat.getIndex());
            f.setArguments(bundle);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mBeatIndex = getArguments().getInt("position", -1);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_beat_list, container, false);
            mProgressBar = (ProgressBar) view.findViewById(R.id.beat_progressbar);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.beat_list_recyclerview);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.beat_list_swiperefreshlayout);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            mCultureAdapter = new CultureAdapter(mBeatIndex);
            mRecyclerView.setAdapter(mCultureAdapter);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST));
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateCulture();
                }
            });
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            updateCulture();
        }

        public void updateCulture() {
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
                        if (mBeatIndex == BEAT.CULTURE.getIndex()) {
                            return NetworkUtil.getInstance().getBeatCulture();
                        } else if (mBeatIndex == BEAT.WELFARE.getIndex()) {
                            return NetworkUtil.getInstance().getBeatWelfare();
                        } else if (mBeatIndex == BEAT.REVIEW.getIndex()) {
                            return NetworkUtil.getInstance().getBeatReview();
                        } else if (mBeatIndex == BEAT.ETC.getIndex()) {
                            return NetworkUtil.getInstance().getBeatEtc();
                        } else if (mBeatIndex == BEAT.QNA.getIndex()) {
                            return NetworkUtil.getInstance().getBeatQna();
                        }
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
                        mCultureAdapter.clearItems();
                        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                        for (Object object : jsonArray) {
                            JSONObject dataObject = (JSONObject) object;
                            String id = dataObject.get("id").toString();
                            String title = dataObject.get("title").toString();
                            String time = dataObject.get("time").toString();
                            mCultureAdapter.addItem(new DefaultBeatItem(id, title, time));
                        }
                        mCultureAdapter.notifyDataSetChanged();
                    }
                }
            }.execute();
        }
    }

    public static class CultureAdapter extends RecyclerView.Adapter<CultureViewHolder> {
        private ArrayList<DefaultBeatItem> list = new ArrayList<>();
        private int mBeatIndex;

        public CultureAdapter(int beatindex) {
            this.mBeatIndex = beatindex;
        }

        public void addItem(DefaultBeatItem item) {
            list.add(item);
        }

        public void clearItems() {
            list.clear();
        }

        @Override
        public CultureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_beat_list_card, parent, false);
            ApplicationUtil.getInstance().setTypeFace(view);
            return new CultureViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CultureViewHolder holder, final int position) {
            DefaultBeatItem beatItem = list.get(position);
            holder.titleView.setText(beatItem.getTitle());
            holder.timeView.setText(getSimpleDetailTime(beatItem.getTime()));
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), BeatDetailActivity.class);
                    intent.putExtra("beatindex", mBeatIndex);
                    intent.putExtra("contentid", list.get(position).getId());
                    v.getContext().startActivity(intent);
//                    Log.d(getClass().getSimpleName(), "Clicked ID : " + list.get(position).getId());
//                    Log.d(getClass().getSimpleName(), "Clicked BEAT : " + mBeatIndex);
                }
            });
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

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public static class CultureViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView, timeView;
        private View rootView;

        public CultureViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.titleView = (TextView) itemView.findViewById(R.id.beat_list_title);
            this.timeView = (TextView) itemView.findViewById(R.id.beat_list_time);
        }
    }

    public static class DefaultBeatItem {
        private String id, title, time;

        public DefaultBeatItem(String id, String title, String time) {
            this.id = id;
            this.title = title;
            this.time = time;
        }

        public String getId() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public String getTime() {
            return this.time;
        }
    }

    /* Look&look 프래그먼트 */
    public static class LooknlookFragment extends Fragment {
        private LooknLookAdapter mLooknLookAdapter;
        private ProgressBar mProgressBar;

        static LooknlookFragment newInstance() {
            LooknlookFragment f = new LooknlookFragment();
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_looknlook, container, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.looknlook_recyclerview);
            mProgressBar = (ProgressBar) view.findViewById(R.id.beat_progressbar);
            mLooknLookAdapter = new LooknLookAdapter(getActivity());
            recyclerView.setAdapter(mLooknLookAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            updateLooknLookData();
        }

        public void updateLooknLookData() {
            new AsyncTask<Void, Void, JSONObject>() {

                @Override
                protected void onPreExecute() {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    try {
                        return NetworkUtil.getInstance().getLooknLook();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    if (jsonObject == null) {
                        return;
                    }
                    String result = jsonObject.get("result").toString();
                    if (result.equals("success")) {
                        JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                        for (Object object : jsonArray) {
                            JSONObject dataJsonObject = (JSONObject) object;
                            String content = dataJsonObject.get("content").toString();
                            String time = dataJsonObject.get("time").toString();

                            JSONArray photoArray = (JSONArray) dataJsonObject.get("photo");
                            String photoData[] = new String[photoArray.size()];

                            for (int i = 0; i < photoData.length; i++) {
                                JSONObject photoJsonObject = (JSONObject) photoArray.get(i);
                                photoData[i] = UrlList.MAIN_URL_IMAGE + photoJsonObject.get("filename").toString();
                            }
                            LooknLookItems lookItems = new LooknLookItems(time, content, photoData);
                            mLooknLookAdapter.addListItem(lookItems);
                        }
                    }
                    mLooknLookAdapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.GONE);
                }
            }.execute();
        }
    }

    public static class LooknLookAdapter extends RecyclerView.Adapter<LooknLookViewHolder> {
        private ArrayList<LooknLookItems> list = new ArrayList<>();
        private FragmentActivity mActivity;
        private Context mContext;

        public LooknLookAdapter(FragmentActivity activity) {
            this.mActivity = activity;
        }

        public void addListItem(LooknLookItems iteme) {
            list.add(iteme);
        }

        @Override
        public LooknLookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_looknlook_card, parent, false);
            ApplicationUtil.getInstance().setTypeFace(view);
            this.mContext = view.getContext();
            return new LooknLookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LooknLookViewHolder holder, int position) {
            holder.getContentView().setText(list.get(position).getContent());
            holder.getTimeView().setText(looknLookSimpleTime(list.get(position).getTime()));

            LinearLayout linearLayout = holder.getPhotoGroup();
            final String[] Imageurls = list.get(position).getImageContent();

            for (int i = 0; i < Imageurls.length; i++) {
                ImageLoaderUtil.getInstance().initImageLoader();
                View imageCardView = LayoutInflater.from(mContext).inflate(R.layout.ui_looknlook_image_card, linearLayout, false);

                final ImageView imageView = (ImageView) imageCardView.findViewById(R.id.imageView);
                final ProgressBar progressBar = (ProgressBar) imageCardView.findViewById(R.id.progressbar);
                final int imagePosition = i;

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ImageCollectionActivity.class);
                        intent.putExtra("Imageurls", Imageurls);
                        intent.putExtra("Position", imagePosition);

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                mActivity, v, "imagecollection_transition");
                        ActivityCompat.startActivity(mActivity, intent, options.toBundle());
                    }
                });
                linearLayout.addView(imageCardView);

                ImageLoader.getInstance().displayImage(Imageurls[i],
                        imageView, ImageLoaderUtil.getInstance().getThumbProfileImageOptions(), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public String looknLookSimpleTime(String srctime) {
            String dataTimeFormat = "yyyy-MM-dd HH:mm:ss";
            String newDateTimeFormat = mContext.getString(R.string.text_beat_looknlook_time_format);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataTimeFormat);
            SimpleDateFormat newDateFormat = new SimpleDateFormat(newDateTimeFormat);

            String time;
            try {
                Date date = simpleDateFormat.parse(srctime);
                time = newDateFormat.format(date);
            } catch (java.text.ParseException ignore) {
                time = srctime;
            }
            return time;
        }
    }

    public static class LooknLookItems {
        private String time, content;
        private String[] imageContent;

        public LooknLookItems(String time, String content, String[] imageContent) {
            this.time = time;
            this.content = content;
            this.imageContent = imageContent;
        }

        public String getTime() {
            return time;
        }

        public String getContent() {
            return content;
        }

        public String[] getImageContent() {
            return imageContent;
        }
    }

    public static class LooknLookViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView contentView, timeView;
        private LinearLayout photoGroup;

        public LooknLookViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.contentView = (TextView) itemView.findViewById(R.id.looknlook_card_content);
            this.timeView = (TextView) itemView.findViewById(R.id.looknlook_card_time);
            this.photoGroup = (LinearLayout) itemView.findViewById(R.id.looknlook_card_photo_group);
        }

        public View getRootView() {
            return this.rootView;
        }

        public TextView getContentView() {
            return contentView;
        }

        public TextView getTimeView() {
            return timeView;
        }

        public LinearLayout getPhotoGroup() {
            return photoGroup;
        }
    }
}
