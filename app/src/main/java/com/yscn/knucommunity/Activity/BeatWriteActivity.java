package com.yscn.knucommunity.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.CustomView.ClearProgressDialog;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Ui.AlertToast;
import com.yscn.knucommunity.Ui.BeatViewPagetAdapter;
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UrlList;
import com.yscn.knucommunity.Util.UserData;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 15. 1. 19..
 */
public class BeatWriteActivity extends ActionBarActivity implements View.OnClickListener {
    private EditText titleView, contentView;
    private int boardType;
    private int GET_PICTURE_RESULT_CODE = 0X10;
    private boolean isEditMode = false;
    private String contentID;
    private HashMap<String, Uri> fileListMap = new HashMap<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_board_write);
        actionBarInit();
        viewInit();

        if (this.isEditMode) {
            setPreContent();
        }
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void viewInit() {
        String boardTypeMessage;
        this.titleView = (EditText) findViewById(R.id.board_write_title);
        this.contentView = (EditText) findViewById(R.id.board_write_content);
        TextView boardTypeView = (TextView) findViewById(R.id.board_write_boardtype);
        this.boardType = getIntent().getIntExtra("boardType", -1);
        this.isEditMode = getIntent().getBooleanExtra("isEditMode", false);

        if (boardType == BeatViewPagetAdapter.BEAT.REVIEW.getIndex()) {
            boardTypeMessage = getString(R.string.text_beat_review);
        } else if (boardType == BeatViewPagetAdapter.BEAT.QNA.getIndex()) {
            boardTypeMessage = getString(R.string.text_beat_qna);
        } else {
            boardTypeMessage = "null";
        }

        boardTypeView.setText(boardTypeMessage);
        findViewById(R.id.board_write_photo_main).setOnClickListener(this);

    }

    private boolean isValidData() {
        if (titleView.getText().toString().isEmpty()) {
            AlertToast.warning(getContext(), getString(R.string.warning_board_write_title));
            return false;
        } else if (contentView.getText().toString().isEmpty()) {
            AlertToast.warning(getContext(), getString(R.string.warning_board_write_content));
            return false;
        } else {
            return true;
        }
    }

    private void setPreContent() {
        contentID = getIntent().getStringExtra("contentid");
        Log.d(getClass().getSimpleName(), "Pre Content : " + contentID);

        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog clearProgressDialog;

            @Override
            protected void onPreExecute() {
                clearProgressDialog = new ClearProgressDialog(getContext());
                clearProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return NetworkUtil.getInstance().checkIsLoginUser().getDefaultboardContent(contentID);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject != null) {
                    String title = jsonObject.get("title").toString();
                    String content = jsonObject.get("content").toString();
                    JSONArray fileArray = (JSONArray) jsonObject.get("file");

                    titleView.setText(title);
                    contentView.setText(content);

                    for (Object fileURL : fileArray) {
                        addBoardPhotoData(fileURL.toString());
                    }
                }
                clearProgressDialog.dismiss();
            }
        }.execute();
    }

    private void actionBarInit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_cancel);

        findViewById(R.id.board_write_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidData()) {
                    writeBoard();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Context getContext() {
        return this;
    }

    private void writeBoard() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getContext(), R.string.error_check_network_state);
                    cancel(true);
                    return;
                }
                dialog = new ClearProgressDialog(getContext());
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject result = null;
                try {
                    String title = titleView.getText().toString();
                    String content = contentView.getText().toString();
                    result = NetworkUtil.getInstance().checkIsLoginUser().writeBeatContent(boardType + 1, title, content,
                            fileListMap, isEditMode, contentID);
                } catch (IOException | ParseException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                dialog.cancel();

                if (jsonObject == null) {
                    AlertToast.error(getContext(), R.string.error_to_work);
                    return;
                }
                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    AlertToast.success(getContext(), R.string.success_board_write);
                    setResult(RESULT_OK);
                    finish();
                }
                if (result.equals("fail")) {
                    String reason = jsonObject.get("reason").toString();
                    if (reason.equals("emptyuserinfo")) {
                        AlertToast.error(getContext(), R.string.error_empty_studentnumber_info);
                        UserData.getInstance().logoutUser();
                    } else {
                        AlertToast.error(getContext(), R.string.error_board_write);
                    }
                }
            }
        }.execute();
    }

    private void getPictureData() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_PICTURE_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_RESULT_CODE && resultCode == RESULT_OK) {
            addBoardPhotodata(data.getData());
        }
    }

    private void addBoardPhotoData(String url) {

        fileListMap.put(url, null);

        final LinearLayout photoDataView = (LinearLayout) findViewById(R.id.board_write_photo_data);
        final View photoChildView = LayoutInflater.from(this).inflate(R.layout.ui_boardwrite_photo, photoDataView, false);
        photoDataView.addView(photoChildView);

        final ImageView photoThumbnailView = (ImageView) photoChildView.findViewById(R.id.board_write_photo_thumbnail);
        final ImageButton photoCancelView = (ImageButton) photoChildView.findViewById(R.id.board_write_photo_cancel);

        photoCancelView.setTag(url);

                /* 사진 썸네일 적용 */
        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(UrlList.BOARD_PHOTO_IMAGE_URL + url, photoThumbnailView,
                ImageLoaderUtil.getInstance().getDefaultOptions());

        photoCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagUri = v.getTag().toString();
                photoDataView.removeView(photoChildView);
                fileListMap.remove(tagUri);
                invalidateScrollView();
            }
        });
        ApplicationUtil.getInstance().setTypeFace(photoChildView);
        invalidateScrollView();
    }

    private void addBoardPhotodata(Uri uri) {
        if (fileListMap.get(uri.toString()) != null) {
            /* 이미 파일이 목록에 있을때 return */
            return;
        }
        fileListMap.put(uri.toString(), uri);
        final LinearLayout photoDataView = (LinearLayout) findViewById(R.id.board_write_photo_data);
        final View photoChildView = LayoutInflater.from(this).inflate(R.layout.ui_boardwrite_photo, photoDataView, false);
        photoDataView.addView(photoChildView);

        final ImageView photoThumbnailView = (ImageView) photoChildView.findViewById(R.id.board_write_photo_thumbnail);
        final TextView photoSizeView = (TextView) photoChildView.findViewById(R.id.board_write_photo_size);
        final TextView photoDiskUsageView = (TextView) photoChildView.findViewById(R.id.board_write_photo_diskusage);
        final ImageButton photoCancelView = (ImageButton) photoChildView.findViewById(R.id.board_write_photo_cancel);
        photoCancelView.setTag(uri.toString());


        /* 이미지 메모리에 올리지를 않고 크기를 구해와서 사진크기와 용량 설정 */
        InputStream input;
        try {
            input = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeStream(input, null, options);
            int height = options.outHeight;
            int width = options.outWidth;
            int fileSize = input.available();

            photoSizeView.setText(width + "x" + height);
            photoDiskUsageView.setText(convertByteToUnitSuffix(fileSize));
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 지우기 버튼 리스너 */
        photoCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagUri = (String) v.getTag();
                Log.d(getClass().getSimpleName(), "Child URI TAG: " + tagUri);
                photoDataView.removeView(photoChildView);
                fileListMap.remove(tagUri);
                invalidateScrollView();
            }
        });

        /* 사진 썸네일 적용 */
        ImageLoaderUtil.getInstance().initImageLoader();
        ImageLoader.getInstance().displayImage(uri.toString(), photoThumbnailView,
                ImageLoaderUtil.getInstance().getDefaultOptions());
        ApplicationUtil.getInstance().setTypeFace(photoChildView);
        invalidateScrollView();
    }

    // 스크롤 뷰가 보여 지고 있을때 사이즈가 0이면 GONE
    // 스크롤 뷰가 보여지지 않고 있을때 사이즈가 0보다 크면 VISIBLE
    private void invalidateScrollView() {
        View scrollView = findViewById(R.id.board_write_photo_scrollview);
        if (scrollView.isShown() && fileListMap.size() == 0) {
            scrollView.setVisibility(View.GONE);
        } else if (!scrollView.isShown() && fileListMap.size() > 0) {
            scrollView.setVisibility(View.VISIBLE);

            // fileList 크기가 1일때 50dp 로 보여줌.
            // fileList 크기가 1보다 클때 100dp 로 보여줌.
            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
            if (fileListMap.size() == 1) {
                layoutParams.height = (int) ApplicationUtil.getInstance().dpToPx(50);
            } else if (fileListMap.size() > 1) {
                layoutParams.height = (int) ApplicationUtil.getInstance().dpToPx(100);
            }
            scrollView.setLayoutParams(layoutParams);
        }
    }

    private String convertByteToUnitSuffix(int src_byte) {
        if (src_byte < 1024) {
            return src_byte + "B";
        } else if (src_byte < 1024 * 1024) {
            return Math.round((float) src_byte / (float) 1024) + "KB";
        } else {
            return Math.round(((float) src_byte / (float) (1024 * 1024) * 10)) / 10.0 + "MB";
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.board_write_photo_main) {
            getPictureData();
        }
    }
}
