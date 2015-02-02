package com.yscn.knucommunity.Ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.Items.LibrarySearchListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 15. 2. 2..
 */
public class LibrarySearchItemAdapter extends RecyclerView.Adapter<LibrarySearchItemAdapter.ViewHolder> {
    private ArrayList<LibrarySearchListItems> mItemses;

    public LibrarySearchItemAdapter(ArrayList<LibrarySearchListItems> itemses) {
        this.mItemses = itemses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ui_librarysearch_book_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ImageLoaderUtil.getInstance().initImageLoader();

        String title = mItemses.get(i).getTitle();
        String callno = mItemses.get(i).getCallno();
        String thumbnail = mItemses.get(i).getBookThumbnail();
        String authorYear = mItemses.get(i).getAuthor() + ", " + mItemses.get(i).getYear();
        String holdingLendTitle = mItemses.get(i).getHolding() + " / " + mItemses.get(i).getLendtitle();

        viewHolder.getTextView().setText(title);
        viewHolder.getCallnoTextView().setText(callno);
        viewHolder.getAuthorYearTextView().setText(authorYear);
        viewHolder.getHoldingLendTextView().setText(holdingLendTitle);
        ImageLoader.getInstance().displayImage(thumbnail, viewHolder.getImageView(), ImageLoaderUtil.getInstance().getDefaultOptions());
    }

    @Override
    public int getItemCount() {
        return mItemses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView, callnoTextView, authorYearTextView, holdingLendTextView;
        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            textView = (TextView) v.findViewById(R.id.library_search_book_title);
            imageView = (ImageView) v.findViewById(R.id.library_search_book_thumbnail);
            callnoTextView = (TextView) v.findViewById(R.id.library_search_book_callno);
            authorYearTextView = (TextView) v.findViewById(R.id.library_search_book_author_year);
            holdingLendTextView = (TextView) v.findViewById(R.id.library_search_book_holding_lendtitle);
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getCallnoTextView() {
            return callnoTextView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public TextView getAuthorYearTextView() {
            return authorYearTextView;
        }

        public TextView getHoldingLendTextView() {
            return holdingLendTextView;
        }
    }
}
