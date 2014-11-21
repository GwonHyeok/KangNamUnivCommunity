package com.yscn.knucommunity.CustomView;

import android.widget.AbsListView;

/**
 * Created by GwonHyeok on 14. 11. 5..
 */
public interface ScrollTabHolder {

    void adjustScroll(int scrollHeight);

    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);
}
