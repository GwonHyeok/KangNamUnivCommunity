package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.MajorSimpleListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Ui.MajorSimpleListAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MajorInfoActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private ListView mListView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        viewInit();
        listViewInit();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void listViewInit() {
        new AsyncTask<Void, Void, ArrayList<MajorSimpleListItems>>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    dialog = new ClearProgressDialog(getContext());
                    dialog.show();
                } else {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(false);
                }
            }

            @Override
            protected ArrayList<MajorSimpleListItems> doInBackground(Void... voids) {
                try {
                    return NetworkUtil.getInstance().getMajorSimpleInfo();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<MajorSimpleListItems> itemses) {
                dialog.cancel();
                if (itemses != null) {
                    ListView listView = mListView;
                    MajorSimpleListAdapter adapter = new MajorSimpleListAdapter(getContext(), android.R.layout.simple_list_item_1, itemses);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(getActivity());
                    listView.setDivider(new ColorDrawable(0xFFBDBDBD));
                    listView.setDividerHeight(1);
                } else {
                    AlertToast.error(getContext(), R.string.error_to_work);
                }
            }
        }.execute();
    }

    private Context getContext() {
        return MajorInfoActivity.this;
    }

    private MajorInfoActivity getActivity() {
        return MajorInfoActivity.this;
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.major_main_primary_dark_color));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.major_main_primary_dark_color));
        }

        /* 액션바 */
        Toolbar toolbar = new Toolbar(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("학과");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.major_main_primary_color));
        toolbar.setNavigationIcon(R.drawable.ic_nav_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mListView = new ListView(this);
        linearLayout.addView(toolbar);
        linearLayout.addView(mListView);
        setContentView(linearLayout);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MajorSimpleListItems items = (MajorSimpleListItems) adapterView.getItemAtPosition(i);
        String majorName = items.getMajorName();
        String majorHomePage = items.getMajorHomepage();
        String majorType = items.getMajorType();
        Intent intent = new Intent(getContext(), MajorDetailActivity.class);
        intent.putExtra("majorName", majorName);
        intent.putExtra("majorHomePage", majorHomePage);
        intent.putExtra("majorType", majorType);
        startActivity(intent);
    }
}
