package com.yscn.knucommunity.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;

/**
 * Created by GwonHyeok on 15. 2. 25..
 */
public class BoardListCategoryDialog extends Dialog {
    private String[] mCategoryItems;
    private TextView okView, cancelView;
    private RecyclerView mCategoryRecyclerView;
    private BoardListCategoryAdapter mBoardListCategoryAdapter;
    private int mCategoryPosition;
    private onCategorySelectListener mOnCategorySelectListener;

    public BoardListCategoryDialog(Context context) {
        super(context);
        initDialog();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_board_category_dialog);
        viewInit();
    }

    private void viewInit() {
        okView = (TextView) findViewById(R.id.board_category_dialog_ok);
        cancelView = (TextView) findViewById(R.id.board_category_dialog_cancel);
        mCategoryRecyclerView = (RecyclerView) findViewById(R.id.board_category_dialog_recyclerview);

        /* init Recycler View */
        mBoardListCategoryAdapter = new BoardListCategoryAdapter();
        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCategoryRecyclerView.setAdapter(mBoardListCategoryAdapter);

        /* setButtonClick Listener */
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BroadCastToListener();
                dismiss();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 아무것도 BroadCasting 하지 않는다 */
                dismiss();
            }
        });

        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void BroadCastToListener() {
        if (this.mOnCategorySelectListener != null) {
            this.mOnCategorySelectListener.onSelectCategory(mCategoryItems[mCategoryPosition], mCategoryPosition + 1);
        }
    }

    public void setCategoryItems(String[] items) {
        this.mCategoryItems = items;
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void setOnCategorySelectListener(onCategorySelectListener categorySelectListener) {
        this.mOnCategorySelectListener = categorySelectListener;
    }

    public interface onCategorySelectListener {
        public void onSelectCategory(String categoryName, int categoryPosition);
    }

    private class BoardListCategoryAdapter extends RecyclerView.Adapter<BoardListCategoryViewHolder> {

        @Override
        public BoardListCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setTextColor(0xFF4285f4);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setBackgroundResource(R.drawable.bg_default_select_item_effect);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            int textViewPadding = (int) ApplicationUtil.getInstance().dpToPx(4f);
            textView.setPadding(textViewPadding, textViewPadding, textViewPadding, textViewPadding);
            ApplicationUtil.getInstance().setTypeFace(textView);
            return new BoardListCategoryViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(BoardListCategoryViewHolder holder, final int position) {
            TextView textView = holder.getCategoryTitleView();
            textView.setText(mCategoryItems[position]);

            /* 만약 텍스트뷰 선택 했을때 Dismiss 후 Listener 로 Broadcast */
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCategoryPosition = position;
                    BroadCastToListener();
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCategoryItems.length;
        }
    }

    private class BoardListCategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView mCategoryTitleView;

        public BoardListCategoryViewHolder(View itemView) {
            super(itemView);
            this.mCategoryTitleView = (TextView) itemView;
        }

        public TextView getCategoryTitleView() {
            return this.mCategoryTitleView;
        }
    }
}
