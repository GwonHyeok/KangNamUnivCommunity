package com.yscn.knucommunity.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.CustomView.MenuBaseActivity;
import com.yscn.knucommunity.CustomView.NotifiableScrollView;
import com.yscn.knucommunity.Items.DeliveryListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Ui.DeliverySpinnerAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GwonHyeok on 14. 11. 4..
 */
public class DeliveryFoodActivity extends MenuBaseActivity {
    private DeliverySpinnerAdapter deliverySpinnerAdapter;
    private int page = 1;
    private String searchText;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_deliveryfood);

        actionBarInit();
        viewInit();
        setContent();
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void setContent() {
        new AsyncTask<Void, Void, ArrayList<DeliveryListItems>>() {

            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    clearProgressDialog = new ClearProgressDialog(DeliveryFoodActivity.this);
                    clearProgressDialog.show();
                } else {
                    refreshLayout.setRefreshing(false);
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(true);
                }
            }

            @Override
            protected ArrayList<DeliveryListItems> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getDeliveryFoodList(page, deliverySpinnerAdapter.getCurrentPosition() + 1, searchText);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<DeliveryListItems> list) {
                if (list != null) {
                    NotifiableScrollView scrollView = (NotifiableScrollView) findViewById(R.id.delivery_scrollview);
                    LinearLayout dataView = (LinearLayout) scrollView.getChildAt(0);
                    ImageLoaderUtil.getInstance().initImageLoader();

                    for (DeliveryListItems item : list) {
                        View view = LayoutInflater.from(DeliveryFoodActivity.this).inflate(R.layout.ui_deliveryfood_card, dataView, false);
                        TextView nameView = (TextView) view.findViewById(R.id.title);
                        TextView phoneView = (TextView) view.findViewById(R.id.phonen_textview);
                        ImageView foodView = (ImageView) view.findViewById(R.id.delivery_food_iamgeview);

                        nameView.setText(item.getName());
                        phoneView.setText(item.getTelnum());
                        ImageLoader.getInstance().displayImage(UrlList.MAIN_URL + item.getImagepath(), foodView, ImageLoaderUtil.getInstance().getNoCacheImageOptions());
                        view.setBackgroundResource(R.drawable.bg_default_select_item_effect);
                        dataView.addView(view);
                        ApplicationUtil.getInstance().setTypeFace(view);
                    }
                } else {
                    AlertToast.error(getContext(), R.string.error_to_work);
                }
                refreshLayout.setRefreshing(false);
                clearProgressDialog.cancel();
            }
        }.execute();
    }

    private void reloadData() {
        NotifiableScrollView scrollView = (NotifiableScrollView) findViewById(R.id.delivery_scrollview);
        LinearLayout dataView = (LinearLayout) scrollView.getChildAt(0);
        dataView.removeAllViews();
        setContent();
    }

    private void viewInit() {
        final Spinner spinner = (Spinner) findViewById(R.id.delivery_spinner);
        String[] deliveryList = getResources().getStringArray(R.array.delivery_list);
        deliverySpinnerAdapter = new DeliverySpinnerAdapter(this, R.layout.ui_deliveryspinner, deliveryList);
        spinner.setAdapter(deliverySpinnerAdapter);
        spinner.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (Build.VERSION.SDK_INT >= 16) {
                    spinner.setDropDownVerticalOffset(spinner.getHeight());
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Change Food Items

                // Set Current Position cause change color selected Item
                deliverySpinnerAdapter.setCurrentPosition(position);

                // May Be Some Phone occur spinner drop down button color
                // white to black when click Other Item
                spinner.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);

                // Reload New Data
                reloadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });

        /* Spinner DropDown Button Color Change */
        spinner.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.board_list_swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });
    }

    private void actionBarInit() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.delivery_main_dark_color));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.delivery_main_dark_color));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        MenuItem searchmenuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchmenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                /*
                 * SearchView 에서 데이터가 입력되고 검색 버튼을 눌렀을때
                 * 스크롤뷰에 있던 내용을 전부 지우고 게시판 리스트 정보를 가져올떄 사용하는 searchText 의 내용 변경 후
                 * 게시판 정보를 가져온다
                 */
                searchText = s;
                reloadData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchmenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                /*
                 * SearchView 가 Collapse 되었을때 검색 정보를 없애고
                 * 다시 게시판 정보를 가져온다.
                 */
                if (searchText != null) {
                    searchText = null;
                    reloadData();
                }
                return true;
            }
        });
        setSearchIconColor(searchView);
        return true;
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
