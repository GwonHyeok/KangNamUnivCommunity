package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
    private Context mContext;
    private String[] mTabTitle;

    public BeatViewPagetAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        mTabTitle = mContext.getResources().getStringArray(R.array.beat_tab_title);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 4) {
            return LooknlookFragment.newInstance();
        } else if (position == 3) {
            return QnAFragment.newInstance();
        } else {
            return CultureFragment.newInstance();
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

    /* 문화 프래그먼트 */
    public static class CultureFragment extends Fragment {

        static CultureFragment newInstance() {
            CultureFragment f = new CultureFragment();
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_main, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }

    /* Q&A 프래그먼트 */
    public static class QnAFragment extends Fragment {
        static QnAFragment newInstance() {
            QnAFragment f = new QnAFragment();
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_freeboard_list, container, false);
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
            mLooknLookAdapter = new LooknLookAdapter();
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
                                photoData[i] = photoJsonObject.get("filename").toString();
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
        private Context mContext;

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
            String[] Imageurls = list.get(position).getImageContent();

            for (String imageurl : Imageurls) {
                ImageLoaderUtil.getInstance().initImageLoader();
                View imageCardView = LayoutInflater.from(mContext).inflate(R.layout.ui_looknlook_image_card, linearLayout, false);

                final ImageView imageView = (ImageView) imageCardView.findViewById(R.id.imageView);
                final ProgressBar progressBar = (ProgressBar) imageCardView.findViewById(R.id.progressbar);
                imageView.setTag(UrlList.MAIN_URL + imageurl);

                linearLayout.addView(imageCardView);

                ImageLoader.getInstance().displayImage(UrlList.MAIN_URL + imageurl,
                        imageView, ImageLoaderUtil.getInstance().getNoCacheImageOptions(), new ImageLoadingListener() {
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
