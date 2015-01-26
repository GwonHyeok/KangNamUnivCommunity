package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardListActivity;
import com.yscn.knucommunity.Items.DefaultBoardListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class GreenLightListActivity extends BaseBoardListActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_greenlight_list);
        super.onCreate(bundle);
    }

    protected void addScrollViewData(ArrayList<DefaultBoardListItems> listItemses) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.greenlight_list);
        View childView = scrollView.getChildAt(0);

        ImageLoaderUtil.getInstance().initImageLoader();

        if (childView instanceof LinearLayout) {
            for (DefaultBoardListItems listItems : listItemses) {
                View listView = LayoutInflater.from(getContext()).inflate(R.layout.ui_greenlightlsit, (ViewGroup) childView, false);
                ((TextView) listView.findViewById(R.id.greenlight_list_title)).setText(getHighLightText(listItems.getTitle()));
                ((TextView) listView.findViewById(R.id.greenlight_list_time)).setText(getSimpleListTime(listItems.getTime()));

                String replyCount = String.format(getString(R.string.community_board_reply_count_form),
                        String.valueOf(listItems.getReplyCount()));
                ((TextView) listView.findViewById(R.id.greenlight_list_reply)).setText(replyCount);

                // 개시글 리스트의 listItems를 View에 태그로 저장
                // 클릭했을 경우에 상세 내용을 보기위해 사용
                listView.setTag(listItems);
                listView.setOnClickListener(this);
                listView.setBackgroundResource(R.drawable.bg_default_select_item_effect);
                ((LinearLayout) childView).addView(listView);
            }
        }

    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.community_greenlight_title);
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.greenlight_main_color);
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.GREENLIGHT;
    }

    @Override
    protected int getBoardListScrollViewID() {
        return R.id.greenlight_list;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {

            if (tag instanceof DefaultBoardListItems) {
                DefaultBoardListItems listItems = (DefaultBoardListItems) tag;
                Intent intent = new Intent(getContext(), GreenLightDetailActivity.class);
                intent.putExtra("contentID", listItems.getContentid());
                intent.putExtra("writerName", listItems.getName());
                intent.putExtra("writerStudentNumber", listItems.getStudentnumber());
                intent.putExtra("title", listItems.getTitle());
                intent.putExtra("time", listItems.getTime());
                startActivity(intent);
            }
        }
    }
}
