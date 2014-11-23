package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.Items.MajorSimpleListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.MajorSimpleListAdapter;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class MajorInfoActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        viewInit();
        listViewInit();
    }

    private void listViewInit() {
        new AsyncTask<Void, Void, ArrayList<MajorSimpleListItems>>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ClearProgressDialog(getContext());
                dialog.show();
            }

            @Override
            protected ArrayList<MajorSimpleListItems> doInBackground(Void... voids) {
                try {
                    return NetworkUtil.getInstance().getMajorSimpleInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<MajorSimpleListItems> itemses) {
                dialog.cancel();
                ListView listView = new ListView(getContext());
                MajorSimpleListAdapter adapter = new MajorSimpleListAdapter(getContext(), android.R.layout.simple_list_item_1, itemses);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(getActivity());
                listView.setDivider(new ColorDrawable(0xFFBDBDBD));
                listView.setDividerHeight(1);
                setContentView(listView);
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
            getWindow().setStatusBarColor(0xFF0097a7);
        }

        /* 액션바 */
        ActionBar actionBar = getSupportActionBar();
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar_base, null);
        ((TextView) view.findViewById(R.id.actionbar_base_title)).setText("학과");
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF0097a7")));
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);
        view.findViewById(R.id.actionbar_base_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MajorInfoActivity.this.finish();
            }
        });
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
