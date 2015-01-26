package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardListActivity;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.Items.DefaultBoardListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FaqListActivity extends BaseBoardListActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_faq_list);
        super.onCreate(bundle);
    }

    protected void addScrollViewData(ArrayList<DefaultBoardListItems> listItemses) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.faq_list);
        View childView = scrollView.getChildAt(0);

        if (childView instanceof LinearLayout) {
            for (DefaultBoardListItems listItems : listItemses) {
                View listView = LayoutInflater.from(getContext()).inflate(R.layout.ui_faqlist, (ViewGroup) childView, false);
                ((TextView) listView.findViewById(R.id.faq_list_title)).setText(getHighLightText(getDefaulttFaqTitle(listItems.getTitle())));
                ((TextView) listView.findViewById(R.id.faq_list_time)).setText(getSimpleListTime(listItems.getTime()));
                ((TextView) listView.findViewById(R.id.faq_reply_size)).setText(
                        getReplyText(String.valueOf(listItems.getReplyCount())));
                ((TextView) listView.findViewById(R.id.faq_list_writer)).setText(listItems.getName());
                ImageView profileImageView = (CircleImageView) listView.findViewById(R.id.faq_list_profile);
                setProfileImage(profileImageView, listItems.getStudentnumber());

                // 개시글 리스트의 listItems를 View에 태그로 저장
                // 클릭했을 경우에 상세 내용을 보기위해 사용
                listView.setTag(listItems);
                listView.setOnClickListener(this);
                listView.setBackgroundResource(R.drawable.bg_default_select_item_effect);
                ((LinearLayout) childView).addView(listView);
            }
        }
    }

    private String getDefaulttFaqTitle(String title) {
        return String.format(getString(R.string.comminity_faq_question_title), title);
    }

    private String getReplyText(String reply) {
        return String.format(getString(R.string.community_faq_reply_txt), reply);
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.community_faq_title);
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.faq_main_color);
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.FAQ;
    }

    @Override
    protected int getBoardListScrollViewID() {
        return R.id.faq_list;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {

            if (tag instanceof DefaultBoardListItems) {
                DefaultBoardListItems listItems = (DefaultBoardListItems) tag;
                Intent intent = new Intent(getContext(), FaqDetailActivity.class);
                intent.putExtra("contentID", listItems.getContentid());
                intent.putExtra("writerName", listItems.getName());
                intent.putExtra("writerStudentNumber", listItems.getStudentnumber());
                intent.putExtra("title", listItems.getTitle());
                intent.putExtra("time", listItems.getTime());
                intent.putExtra("replyCount", listItems.getReplyCount());
                startActivity(intent);
            }
        }
    }
}
