package com.yscn.knucommunity.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.CircleImageView;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.Items.DefaultBoardListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class FaqListActivity extends MenuBaseActivity implements View.OnClickListener {
    private int pageIndex = 1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_faq_list);
        actionBarInit();
        getListData();
    }

    private void getListData() {
        new AsyncTask<Void, Void, ArrayList<DefaultBoardListItems>>() {
            private ClearProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ClearProgressDialog(getContext());
                progressDialog.show();
            }

            @Override
            protected ArrayList<DefaultBoardListItems> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getDefaultboardList(
                            NetworkUtil.BoardType.FAQ, pageIndex);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<DefaultBoardListItems> listItemses) {
                if (listItemses != null) {
                    addScrollViewData(listItemses);
                }
                progressDialog.cancel();
            }
        }.execute();
    }

    private void addScrollViewData(ArrayList<DefaultBoardListItems> listItemses) {
        ScrollView scrollView = (ScrollView) findViewById(R.id.faq_list);
        View childView = scrollView.getChildAt(0);

        ImageLoaderUtil.getInstance().initImageLoader();

        if (childView instanceof LinearLayout) {
            String dataTimeFormat = "yyyy-MM-dd hh:mm:ss";
            String newDateTimeFormat = "yyyy.MM.dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataTimeFormat);
            SimpleDateFormat newDateFormat = new SimpleDateFormat(newDateTimeFormat);

            String time;
            for (DefaultBoardListItems listItems : listItemses) {
                try {
                    Date date = simpleDateFormat.parse(listItems.getTime());
                    time = newDateFormat.format(date);
                } catch (java.text.ParseException ignore) {
                    // Date Parse Exception
                    time = listItems.getTime();
                }

                View listView = LayoutInflater.from(getContext()).inflate(R.layout.ui_faqlist, (ViewGroup) childView, false);
                ((TextView) listView.findViewById(R.id.faq_list_title)).setText(getDefaulttFaqTitle(listItems.getTitle()));
                ((TextView) listView.findViewById(R.id.faq_list_time)).setText(time);
                ((TextView) listView.findViewById(R.id.faq_reply_size)).setText(
                        getReplyText(String.valueOf(listItems.getReplyCount())));
                ((TextView) listView.findViewById(R.id.faq_list_writer)).setText(listItems.getName());
                ImageView profileImageView = (CircleImageView) listView.findViewById(R.id.faq_list_profile);

                ImageLoader.getInstance().displayImage(
                        UrlList.PROFILE_IMAGE_URL + listItems.getStudentnumber(), profileImageView,
                        ImageLoaderUtil.getInstance().getDefaultOptions());

                // 개시글 리스트의 listItems를 View에 태그로 저장
                // 클릭했을 경우에 상세 내용을 보기위해 사용
                listView.setTag(listItems);
                listView.setOnClickListener(this);
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

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.faq_main_color));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle(getString(R.string.community_faq_title));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_nav_menu_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
