package com.yscn.knucommunity.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by GwonHyeok on 15. 1. 10..
 */
public class NotifiableScrollView extends ScrollView {
    private Rect m_Rect;
    private onScrollListener m_onScrollListener;

    public NotifiableScrollView(Context context) {
        super(context);
    }

    public NotifiableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifiableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        checkScrollViewFooter();
    }

    private void checkScrollViewFooter() {
        if (m_Rect == null) {
            m_Rect = new Rect();
            getLocalVisibleRect(m_Rect);
            return;
        }

        int oldBottom = m_Rect.bottom;
        getLocalVisibleRect(m_Rect);
        int height = getMeasuredHeight();
        View v = getChildAt(0);
        if (oldBottom > 0 && height > 0) {
            if (oldBottom != m_Rect.bottom && m_Rect.bottom == (v.getMeasuredHeight() +
                    getPaddingTop() + getPaddingBottom())) {
                if (m_onScrollListener != null) {
                    this.m_onScrollListener.scrollToBottom();
                }
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.m_onScrollListener != null) {
            this.m_onScrollListener.onScroll(this, l, t, oldl, oldt);
        }
    }

    public void setonScrollToBottomListener(onScrollListener onscrollListener) {
        this.m_onScrollListener = onscrollListener;
    }

    public interface onScrollListener {
        public void scrollToBottom();

        public void onScroll(ScrollView view, int l, int t, int oldl, int oldt);
    }
}
