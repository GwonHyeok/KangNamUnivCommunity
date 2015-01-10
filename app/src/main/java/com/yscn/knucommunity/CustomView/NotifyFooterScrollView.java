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
public class NotifyFooterScrollView extends ScrollView {
    private Rect m_Rect;
    private onScrollToBottomListener m_onScrollToBottomListener;

    public NotifyFooterScrollView(Context context) {
        super(context);
    }

    public NotifyFooterScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifyFooterScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                if (m_onScrollToBottomListener != null) {
                    this.m_onScrollToBottomListener.scrollToBottom();
                }
            }
        }
    }

    public void setonScrollToBottomListener(onScrollToBottomListener m_onScrollToBottomListener) {
        this.m_onScrollToBottomListener = m_onScrollToBottomListener;
    }

    public interface onScrollToBottomListener {
        public void scrollToBottom();
    }

}
