package com.yscn.knucommunity.CustomView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.Activity.CommunittyActivity;
import com.yscn.knucommunity.Activity.LinkActivity;
import com.yscn.knucommunity.Activity.MarketMainActivity;
import com.yscn.knucommunity.Activity.NoticeActivity;
import com.yscn.knucommunity.Activity.SettingActivity;
import com.yscn.knucommunity.Activity.StudentGroundActivity;
import com.yscn.knucommunity.Activity.StudentInfoActivity;
import com.yscn.knucommunity.Activity.StudentNotificationActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UserData;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * Created by GwonHyeok on 15. 2. 23..
 */
public class BaseNavigationDrawerActivity extends ActionBarActivity {
    protected ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;
    protected Toolbar mToolbar;
    private TextView mNicknameView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_base_navigationdrawer);
        ApplicationUtil.getInstance().setTypeFace(findViewById(R.id.navigationdrawer_drawerlayout));
        drawerInit();
    }

    private void drawerInit() {
        /* set profile image */
        ImageView profileImageView = (ImageView) findViewById(R.id.navigationdrawer_profileimage);
        setProfileImage(profileImageView, UserData.getInstance().getStudentNumber());

        TextView nameTextview = (TextView) findViewById(R.id.navigationdrawer_name);
        nameTextview.setText(UserData.getInstance().getStudentName());

        mNicknameView = (TextView) findViewById(R.id.navigationdrawer_nickname);
        setNickname();

        /* 사용자 정보 */
        findViewById(R.id.navigationdrawer_studentinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), StudentInfoActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_mynotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), StudentNotificationActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), NoticeActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_studentground).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), StudentGroundActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_market).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), MarketMainActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_community).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), CommunittyActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), LinkActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(new Intent(getContext(), SettingActivity.class));
                ApplicationUtil.getInstance().finishAllActivity();
            }
        });

        findViewById(R.id.navigationdrawer_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.warning_title)
                        .setMessage(R.string.text_are_you_want_logout_message)
                        .setNegativeButton(R.string.NO, null)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logoutUser();
                            }
                        })
                        .show();
                ApplicationUtil.getInstance().finishAllActivity();
                ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
            }
        });
    }

    private void logoutUser() {
        new AsyncTask<Void, Void, Boolean>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                if (ApplicationUtil.getInstance().isOnlineNetwork()) {
                    clearProgressDialog = new ClearProgressDialog(getApplicationContext());
                    clearProgressDialog.show();
                } else {
                    AlertToast.error(getApplicationContext(), R.string.error_check_network_state);
                    cancel(true);
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    NetworkUtil.getInstance().checkIsLoginUser().doLogout();
                    return true;
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean bool) {
                clearProgressDialog.cancel();
                if (bool) {
                    UserData.getInstance().logoutUser();
                }
            }
        }.execute();
    }

    private void startNewActivity(Intent intent) {
        finish();
        startActivity(intent);
    }

    protected void setProfileImage(ImageView profileImageView, String studentNumber) {
        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(
                NetworkUtil.getInstance().getProfileThumbURL(studentNumber), profileImageView,
                ImageLoaderUtil.getInstance().getDefaultOptions());
    }

    @Override
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        if (mToolbar != null) {
            mDrawerToggle.syncState();
        }
    }

    protected Context getContext() {
        return this;
    }

    protected void setNickname() {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                String nickName = UserData.getInstance().getStudentNickname();
                if (nickName != null) {
                    mNicknameView.setText(nickName);
                    cancel(true);
                }
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().getSimpleProfile();
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }

                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    UserData.getInstance().setStudentNickname(jsonObject.get("nickname").toString());
                    mNicknameView.setText(UserData.getInstance().getStudentNickname());
                }
            }
        }.execute();
    }

    protected void attatchView(int layoutid) {
//        Log.d(getClass().getSimpleName(), "ATTATCH VIEW");
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.navigationdrawer_content_root);
        View view = LayoutInflater.from(this).inflate(layoutid, frameLayout, false);
        frameLayout.addView(view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigationdrawer_drawerlayout);

        if (mToolbar != null) {
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    mToolbar, R.string.app_name, R.string.app_name) {

                @Override
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };

            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
            ApplicationUtil.getInstance().setTypeFace(mToolbar);
        } else {
//            Log.d(getClass().getSimpleName(), "TOOL BAR IS NULL");
        }
    }
}
