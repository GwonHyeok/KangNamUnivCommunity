package com.yscn.knucommunity.Activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yscn.knucommunity.CustomView.PieGraph;
import com.yscn.knucommunity.CustomView.PieSlice;
import com.yscn.knucommunity.Items.LibrarySeatItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 3..
 */
public class LibraryActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_library_main);

        viewInit();
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xff3d3d3d);
        }
        getSupportActionBar().hide();
        findViewById(R.id.library_find).setOnClickListener(this);
        findViewById(R.id.library_usage).setOnClickListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.library_container, new FindFragment()).commit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.library_find) {
            getSupportFragmentManager().beginTransaction().replace(R.id.library_container, new FindFragment()).commit();
        } else if (id == R.id.library_usage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.library_container, new GraphFragment()).commit();
        }
    }

    public static class FindFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            return inflater.inflate(R.layout.activity_libraryfind, container, false);
        }
    }

    public static class GraphFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.activity_libraryusage, container, false);

            new AsyncTask<Void, Void, ArrayList<LibrarySeatItems>>() {
                private ProgressDialog dialog;

                @Override
                protected void onPreExecute() {
                    dialog = new ProgressDialog(getActivity());
                    dialog.setIndeterminate(true);
                    dialog.show();
                }

                @Override
                protected ArrayList<LibrarySeatItems> doInBackground(Void... voids) {
                    try {
                        return NetworkUtil.getInstance().getLibrarySeatInfo();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(ArrayList<LibrarySeatItems> itemses) {
                    PieGraph pieGraph = (PieGraph) view.findViewById(R.id.library_usage_piegraph);
                    PieSlice slice = new PieSlice();
                    slice.setColor(Color.parseColor("#2979FF"));
                    slice.setValue(itemses.get(0).getUseSeat());
                    pieGraph.addSlice(slice);
                    slice = new PieSlice();
                    slice.setColor(Color.parseColor("#525252"));
                    slice.setValue(itemses.get(0).getEmptySeat());
                    pieGraph.addSlice(slice);
                    pieGraph.setThickness(100);

                    PieGraph pieGraph1 = (PieGraph) view.findViewById(R.id.library_usage_piegraph1);
                    PieSlice slice1 = new PieSlice();
                    slice1.setColor(Color.parseColor("#EC407A"));
                    slice1.setValue(itemses.get(1).getUseSeat());
                    pieGraph1.addSlice(slice1);
                    slice1 = new PieSlice();
                    slice1.setColor(Color.parseColor("#525252"));
                    slice1.setValue(itemses.get(1).getEmptySeat());
                    pieGraph1.addSlice(slice1);
                    pieGraph1.setThickness(100);

                    PieGraph pieGraph2 = (PieGraph) view.findViewById(R.id.library_usage_piegraph2);
                    PieSlice slice2 = new PieSlice();
                    slice2.setColor(Color.parseColor("#7CB342"));
                    slice2.setValue(itemses.get(2).getUseSeat());
                    pieGraph2.addSlice(slice2);
                    slice2 = new PieSlice();
                    slice2.setColor(Color.parseColor("#525252"));
                    slice2.setValue(itemses.get(1).getEmptySeat());
                    pieGraph2.addSlice(slice2);
                    pieGraph2.setThickness(100);

                    dialog.cancel();
                }
            }.execute();
            return view;
        }
    }
}
