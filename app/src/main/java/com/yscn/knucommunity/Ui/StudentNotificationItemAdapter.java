package com.yscn.knucommunity.Ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.Activity.FaqDetailActivity;
import com.yscn.knucommunity.Activity.FreeBoardDetailActivity;
import com.yscn.knucommunity.Activity.GreenLightDetailActivity;
import com.yscn.knucommunity.Activity.MeetingDetailActivity;
import com.yscn.knucommunity.Activity.ShareTaxiDetailActivity;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.Items.StudentNotificationItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 15. 2. 3..
 */
public class StudentNotificationItemAdapter extends RecyclerView.Adapter<StudentNotificationItemAdapter.ViewHolder> {
    private ArrayList<StudentNotificationItems> itemses = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ui_studentnotification_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        StudentNotificationItems notificationItem = itemses.get(i);
        viewHolder.getTitleView().setText(notificationItem.getTitle());
        viewHolder.getTimeView().setText(notificationItem.getTime());

        if (notificationItem.getType() == StudentNotificationItems.Type.Notify) {
            ImageLoaderUtil.getInstance().initImageLoader();
            ImageView imageView = viewHolder.getProfileView();
            imageView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(
                    NetworkUtil.getInstance().getProfileThumbURL(notificationItem.getWriter()),
                    imageView,
                    ImageLoaderUtil.getInstance().getThumbProfileImageOptions());
        }
        setItemClickLietener(notificationItem.getBoardid(), notificationItem.getContentid(), viewHolder.getRootView());
        ApplicationUtil.getInstance().setTypeFace(viewHolder.getRootView());
    }

    private void setItemClickLietener(final String boardid, final String contentid, View rootView) {
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = v.getContext();
                Intent intent = null;

                switch (boardid) {
                    case "1":
                        intent = new Intent(mContext, FreeBoardDetailActivity.class);
                        break;
                    case "2":
                        intent = new Intent(mContext, FaqDetailActivity.class);
                        break;
                    case "3":
                        intent = new Intent(mContext, GreenLightDetailActivity.class);
                        break;
                    case "4":
                        intent = new Intent(mContext, MeetingDetailActivity.class);
                        break;
                    case "5":
                        intent = new Intent(mContext, ShareTaxiDetailActivity.class);
                        break;
                    case "6":
                        intent = new Intent(mContext, FreeBoardDetailActivity.class);
                        break;
                }

                if (intent != null) {
                    intent.putExtra("contentID", contentid);
                    mContext.startActivity(intent);
                } else {
                    AlertToast.error(mContext, R.string.error_to_work);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemses.size();
    }

    public ArrayList<StudentNotificationItems> getItemses() {
        return this.itemses;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView, timeView;
        private ImageView profileView;
        private RelativeLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.titleView = (TextView) itemView.findViewById(R.id.studentnotify_item_title);
            this.timeView = (TextView) itemView.findViewById(R.id.studentnotify_item_time);
            this.rootView = (RelativeLayout) itemView.findViewById(R.id.studentnotify_item_root);
            this.profileView = (CircleImageView) itemView.findViewById(R.id.studentnotify_item_imageview);
        }

        public TextView getTitleView() {
            return titleView;
        }

        public RelativeLayout getRootView() {
            return rootView;
        }

        public TextView getTimeView() {
            return timeView;
        }

        public ImageView getProfileView() {
            return profileView;
        }
    }
}
