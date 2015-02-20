package com.yscn.knucommunity.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yscn.knucommunity.Items.NoticeItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class NoticeListFragment extends ScrollTabHolderFragment implements OnScrollListener {

    private static final String ARG_POSITION = "position";
    private static HashMap<String, ArrayList<NoticeItems>> mItemes;
    private ListView mListView;
    private ArrayList<NoticeItems> mListItems;
    private int mPosition;

    public synchronized static Fragment newInstance(int position, HashMap<String, ArrayList<NoticeItems>> itemes) {
        NoticeListFragment f = new NoticeListFragment();
        mItemes = itemes;
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);
        if (mPosition == 0) {
            mListItems = mItemes.get("notice");
        } else if (mPosition == 1) {
            mListItems = mItemes.get("haksa");
        } else if (mPosition == 2) {
            mListItems = mItemes.get("janghak");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_fragment_list, null);

        mListView = (ListView) v.findViewById(R.id.listView);

        View placeHolderView = inflater.inflate(R.layout.custom_view_header_placeholder, mListView, false);
        mListView.addHeaderView(placeHolderView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setOnScrollListener(this);
        mListView.setAdapter(new NoticeListAdapter(getActivity(), android.R.layout.simple_list_item_1, mListItems));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoticeItems noticeItems = (NoticeItems) parent.getItemAtPosition(position);
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(noticeItems.getUrl()));
                startActivity(myIntent);

            }
        });
    }

    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }

        mListView.setSelectionFromTop(1, scrollHeight);

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    private class NoticeListAdapter extends ArrayAdapter<NoticeItems> {

        public NoticeListAdapter(Context context, int resource, ArrayList<NoticeItems> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            View view = convertView;
            AdapterHolder holder;
            if (view == null) {
                LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                int paddingLR = (int) ApplicationUtil.getInstance().dpToPx(10);
                int paddingTB = (int) ApplicationUtil.getInstance().dpToPx(4);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                holder = new AdapterHolder();
                holder.title = (TextView) view.findViewById(android.R.id.text1);
                holder.title.setTextColor(0xff474747);

                view.setTag(holder);
                ApplicationUtil.getInstance().setTypeFace(view);
            } else {
                holder = (AdapterHolder) view.getTag();
            }
            NoticeItems object = getItem(position);
            holder.title.setText(object.getTitle());
            return view;
        }

        private class AdapterHolder {
            TextView title;
        }
    }
}