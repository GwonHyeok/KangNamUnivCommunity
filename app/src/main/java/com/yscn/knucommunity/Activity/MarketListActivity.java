package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.BaseBoardListActivity;
import com.yscn.knucommunity.CustomView.BoardListCategoryDialog;
import com.yscn.knucommunity.CustomView.FloatingActionButton;
import com.yscn.knucommunity.CustomView.FloatingActionsMenu;
import com.yscn.knucommunity.Items.DefaultBoardListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MarketListActivity extends BaseBoardListActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        attatchView(R.layout.activity_shop_list);
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
        viewInit();
    }

    private void viewInit() {
        final String[] items = getResources().getStringArray(R.array.market_category);
        final BoardListCategoryDialog categoryDialog = new BoardListCategoryDialog(getContext());
        categoryDialog.setCategoryItems(items);
        categoryDialog.setOnCategorySelectListener(new BoardListCategoryDialog.onCategorySelectListener() {
            @Override
            public void onSelectCategory(String categoryName, int categoryPosition) {
                setCategory(categoryPosition);
                reloadViewData();
            }
        });

        final FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.shop_menu_button);
        attatchFloatingButton(floatingActionsMenu);

        FloatingActionButton categoryButton = (FloatingActionButton) findViewById(R.id.shop_category_button);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog.show();
                floatingActionsMenu.collapse();
            }
        });

        FloatingActionButton allButton = (FloatingActionButton) findViewById(R.id.shop_all_button);
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCategory(0);
                reloadViewData();
                floatingActionsMenu.collapse();
            }
        });
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.community_market_title);
    }

    @Override
    protected NetworkUtil.BoardType getBoardType() {
        return NetworkUtil.BoardType.MARKET;
    }

    @Override
    protected int getBoardListScrollViewID() {
        return R.id.shop_scrollview;
    }

    @Override
    protected void addScrollViewData(ArrayList<DefaultBoardListItems> listItemses) {
        ScrollView scrollView = (ScrollView) findViewById(getBoardListScrollViewID());
        View childView = scrollView.getChildAt(0);

        if (childView instanceof LinearLayout) {
            for (final DefaultBoardListItems listItems : listItemses) {
                Log.d(getClass().getSimpleName(), listItems.getTitle());
                View listView = LayoutInflater.from(getContext()).inflate(R.layout.ui_marketillist, (ViewGroup) childView, false);
                ((TextView) listView.findViewById(R.id.market_list_title)).setText(getHighLightText(listItems.getTitle()));
                ((TextView) listView.findViewById(R.id.market_list_time)).setText(getSimpleListTime(listItems.getTime()));
                ((TextView) listView.findViewById(R.id.market_list_reply)).setText(String.valueOf(listItems.getReplyCount()));
                ((TextView) listView.findViewById(R.id.market_list_name)).setText(listItems.getName());

                // 개시글 리스트의 listItems를 View에 태그로 저장
                // 클릭했을 경우에 상세 내용을 보기위해 사용
                listView.setTag(listItems);
                listView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MarketBoardDetailActivity.class);
                        intent.putExtra("contentID", listItems.getContentid());
                        intent.putExtra("writerName", listItems.getName());
                        intent.putExtra("writerStudentNumber", listItems.getStudentnumber());
                        intent.putExtra("title", listItems.getTitle());
                        intent.putExtra("time", listItems.getTime());
                        startActivity(intent);
                    }
                });
                listView.setBackgroundResource(R.drawable.bg_default_select_item_effect);
                ((LinearLayout) childView).addView(listView);
                ApplicationUtil.getInstance().setTypeFace(listView);
            }
        }
    }
}
