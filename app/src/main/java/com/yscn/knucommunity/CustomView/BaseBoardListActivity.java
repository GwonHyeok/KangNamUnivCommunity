package com.yscn.knucommunity.CustomView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.Activity.BoardWriteActivity;
import com.yscn.knucommunity.Items.DefaultBoardListItems;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by GwonHyeok on 15. 1. 10..
 */
public abstract class BaseBoardListActivity extends BaseNavigationDrawerActivity {
    private final int BOARD_WRITE_RESPONSE = 0X01;
    protected SwipeRefreshLayout swipeRefreshLayout;
    private int pageIndex = 1;
    private String searchText = null;

    @Override
    public void onStart() {
        super.onStart();
        scrollViewInit();
        getBoardListData();
        setToolbarTitle();
    }

    private void setToolbarTitle() {
        mToolbar.setTitle(getActionBarTitle());
        mToolbar.setTitleTextColor(0xffffffff);
    }

    private void scrollViewInit() {
        View view = findViewById(getBoardListScrollViewID());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.board_list_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadViewData();
            }
        });
        if (view instanceof NotifiableScrollView) {
            final NotifiableScrollView scrollView = (NotifiableScrollView) view;

            scrollView.setonScrollToBottomListener(new NotifiableScrollView.onScrollListener() {
                @Override
                public void scrollToBottom() {
                    // 자료가 15 * pageindex 보다 적으면 호출 하지 않음.
                    // 자료가 15 * pageindec == 15 * pageindex 일때 pageIndex 하나 올리고 데이터 로딩 호출.

                    View view = scrollView.getChildAt(0);
                    if (view instanceof LinearLayout) {
                        int childSize = ((LinearLayout) view).getChildCount();
                        if (childSize == pageIndex * 15) {
                            setPageIndex(pageIndex + 1);
                            getBoardListData();
                        }
                    }
                }

                @Override
                public void onScroll(ScrollView view, int l, int t, int oldl, int oldt) {

                }
            });
        } else {
            Log.d(getClass().getSimpleName(), "ScrollView is Must NotifyFooterScrollView");
        }
    }

    /**
     * 스크롤 뷰에 있는 데이터를 전부 지우고
     * 페이지 인덱스를 1로 변경
     */
    private void removeAllListContent() {
        setPageIndex(1);
        View view = findViewById(getBoardListScrollViewID());
        if (view instanceof NotifiableScrollView) {
            NotifiableScrollView scrollView = (NotifiableScrollView) view;
            LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
            linearLayout.removeAllViews();
        }
    }

    protected Spannable getHighLightText(String defaultString) {
        Spannable spannableString = new SpannableString(defaultString);
        if (this.searchText != null) {
            int index, length;
            length = searchText.length();
            index = defaultString.indexOf(searchText);

            while (index >= 0) {
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), index, index + length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = defaultString.indexOf(searchText, index + 1);
            }
        }
        return spannableString;
    }

    /**
     * 게시판 리스트 정보 가져옴
     */
    protected void getBoardListData() {
        new AsyncTask<Void, Void, ArrayList<DefaultBoardListItems>>() {
            private ClearProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                boolean isOnline = ApplicationUtil.getInstance().isOnlineNetwork();
                if (!isOnline) {
                    cancel(true);
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                progressDialog = new ClearProgressDialog(getContext());
                if (!swipeRefreshLayout.isRefreshing()) {
                    progressDialog.show();
                }
            }

            @Override
            protected ArrayList<DefaultBoardListItems> doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().getDefaultboardList(getBoardType(), getPageIndex(), searchText);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<DefaultBoardListItems> listItemses) {
                if (listItemses == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                }
                if (listItemses != null) {
                    addScrollViewData(listItemses);
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                progressDialog.cancel();
            }
        }.execute();
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
                reloadViewData();
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
                    reloadViewData();
                }
                return true;
            }
        });
        setSearchIconColor(searchView);
        ApplicationUtil.getInstance().setTypeFace(searchView);
        return true;
    }

    protected void reloadViewData() {
        removeAllListContent();
        getBoardListData();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_write) {
            Intent intent = new Intent(getContext(), BoardWriteActivity.class);
            intent.putExtra("boardType", getBoardType().getValue());
            startActivityForResult(intent, BOARD_WRITE_RESPONSE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BOARD_WRITE_RESPONSE && resultCode == RESULT_OK) {
            reloadViewData();
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * @param profileImageView BoardList profileImageView
     * @param studentNumber    addScrollViewData(ArrayList<DefaultBoardListItems> listItemses)
     *                         의 어레이에서 학번 정보를 받아와서 넘긴다.
     */
    protected void setProfileImage(ImageView profileImageView, String studentNumber) {
        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(
                NetworkUtil.getInstance().getProfileThumbURL(studentNumber), profileImageView,
                ImageLoaderUtil.getInstance().getDefaultOptions());
    }

    /**
     * @return get page Index
     */
    protected int getPageIndex() {
        return this.pageIndex;
    }

    /**
     * @param pageIndex 보드 정보를 가져올떄 인덱스 값.
     */
    protected void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * @return ActionBarTitle Message
     */
    protected abstract String getActionBarTitle();

    /**
     * @return get BoardType
     */
    protected abstract NetworkUtil.BoardType getBoardType();

    /**
     * board list scrollView must NotifyFooterScrollView
     *
     * @return board list ScrollView ID
     */
    protected abstract int getBoardListScrollViewID();

    /**
     * 각 레이아웃들의 갑들을 넣어줌
     * 제목, 내용, 시간, 이름, 프로필 이미지 등...
     *
     * @param listItemses is getBoardListData List Data
     */
    protected abstract void addScrollViewData(ArrayList<DefaultBoardListItems> listItemses);

    /**
     * @param deftime db상의 기본 DateTime 폼
     * @return 기본 보드에서 사용하는 시간폼으로 변경하여 리턴
     */
    protected String getSimpleListTime(String deftime) {
        String dataTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String newDateTimeFormat = "yyyy.MM.dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataTimeFormat);
        SimpleDateFormat newDateFormat = new SimpleDateFormat(newDateTimeFormat);

        String time;
        try {
            Date date = simpleDateFormat.parse(deftime);
            time = newDateFormat.format(date);
        } catch (java.text.ParseException ignore) {
            // Date Parse Exception
            time = deftime;
        }
        return time;
    }
}