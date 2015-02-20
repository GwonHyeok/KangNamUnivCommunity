package com.yscn.knucommunity.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.PieGraph;
import com.yscn.knucommunity.CustomView.PieSlice;
import com.yscn.knucommunity.Items.LibrarySearchListItems;
import com.yscn.knucommunity.Items.LibrarySeatItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.LibrarySearchItemAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

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
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void viewInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.library_search_primary_dark_color));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.library_search_primary_dark_color));
        }
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

    @Override
    public void onBackPressed() {
        Fragment searchFragment = getSupportFragmentManager().findFragmentByTag("search");
        if (searchFragment == null) {
            super.onBackPressed();
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.remove(searchFragment);
            transaction.commit();
        }
    }

    public static class FindFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            View view = inflater.inflate(R.layout.activity_libraryfind, container, false);
            ApplicationUtil.getInstance().setTypeFace(view);
            EditText editText = (EditText) view.findViewById(R.id.library_edittext);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchBook(v.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
            return view;
        }

        private void searchBook(String bookKeyword) {
            SearchDataFragment fragment = new SearchDataFragment();
            Bundle bundle = new Bundle();
            bundle.putString("keyword", bookKeyword);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.library_root, fragment, "search");
            transaction.commit();
        }
    }

    public static class SearchDataFragment extends Fragment {

        protected RecyclerView.LayoutManager mLayoutManager;
        private RecyclerView recyclerView;
        private LibrarySearchItemAdapter searchItemAdapter;
        private ArrayList<LibrarySearchListItems> itemses = new ArrayList<>();
        private int pastVisiblesItems, visibleItemCount, totalItemCount;
        private boolean isRefresh = true;
        private int page = 1;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            View view = inflater.inflate(R.layout.activity_librarysearch, container, false);
            ApplicationUtil.getInstance().setTypeFace(view);
            final String bookKeyword = getArguments().getString("keyword");
            recyclerView = (RecyclerView) view.findViewById(R.id.librarysearch_recycleview);
            /* Page Finish Listener */
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

                    if (isRefresh) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isRefresh = false;
                            bookDataInit(bookKeyword, ++page);
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
            mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            searchItemAdapter = new LibrarySearchItemAdapter(itemses);
            recyclerView.setAdapter(searchItemAdapter);

            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            setHasOptionsMenu(true);

            bookDataInit(bookKeyword, page);
            return view;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            Log.d(getClass().getSimpleName(), "onCreateOptionsMenu");
            super.onCreateOptionsMenu(menu, menuInflater);
            menuInflater.inflate(R.menu.board_menu, menu);

            /* Hide Write Button */
            menu.getItem(1).setVisible(false);

            MenuItem searchmenuItem = menu.findItem(R.id.action_search);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchmenuItem);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.requestFocus();
            setSearchIconColor(searchView);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    page = 1;
                    bookDataInit(s, page);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return true;
                }
            });
            ApplicationUtil.getInstance().setTypeFace(searchView);
        }

        private void bookDataInit(final String bookKeyword, final int page) {
            new AsyncTask<Void, Void, JSONObject>() {
                ClearProgressDialog clearProgressDialog;

                @Override
                protected void onPreExecute() {
                    clearProgressDialog = new ClearProgressDialog(getActivity());
                    clearProgressDialog.show();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    try {
                        return NetworkUtil.getInstance().getServerLibrary(bookKeyword, String.valueOf(page));
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    clearProgressDialog.cancel();
                    if (page == 1) {
                        itemses.clear();
                    }
                    JSONArray rootArray = (JSONArray) jsonObject.get("data");
                    for (Object object : rootArray) {
                        JSONObject dataObject = (JSONObject) object;
                        String thumbnail = dataObject.get("l_image_url").toString();
                        String title = dataObject.get("l_title").toString();
                        String author = dataObject.get("l_author").toString();
                        String year = dataObject.get("l_year").toString();
                        String callno = dataObject.get("l_callno").toString();
                        String holding = dataObject.get("l_holding").toString();
                        String lendtitle = dataObject.get("l_lendtitle").toString();
                        itemses.add(new LibrarySearchListItems(thumbnail, title, callno, author, year, holding, lendtitle));
                    }
                    searchItemAdapter.notifyDataSetChanged();
                    if (rootArray.size() != 0) {
                        isRefresh = true;
                    }
                }
            }.execute();
        }

        /**
         * SearchView 이미지 색 변경
         *
         * @param searchView SearchView MenuItem
         */
        private void setSearchIconColor(SearchView searchView) {
            LinearLayout ll = (LinearLayout) searchView.getChildAt(0);
            LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
            LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
            SearchView.SearchAutoComplete autoComplete = ((SearchView.SearchAutoComplete) ll3.getChildAt(0));
            ImageView searchCloseButton = (ImageView) ll3.getChildAt(1);
            ImageView labelView = (ImageView) ll.getChildAt(1);

            autoComplete.setTextColor(Color.WHITE);
            searchCloseButton.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            labelView.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    public static class GraphFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.activity_libraryusage, container, false);
            ApplicationUtil.getInstance().setTypeFace(view);

            new AsyncTask<Void, Void, ArrayList<LibrarySeatItems>>() {
                private ClearProgressDialog dialog;

                @Override
                protected void onPreExecute() {
                    dialog = new ClearProgressDialog(getActivity());
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
