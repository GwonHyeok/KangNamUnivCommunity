package com.yscn.knucommunity.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.yscn.knucommunity.Util.ApplicationUtil;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.NetworkUtil;
import com.yscn.knucommunity.Util.UserData;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by GwonHyeok on 15. 2. 20..
 */
public class DeliveryFoodWriteActivity extends ActionBarActivity {
    private TextView categoryTextView;
    private int selectedCategoryItem;
    private int GET_PICTURE_RESULT_CODE = 0X10;
    private HashMap<String, Uri> fileListMap = new HashMap<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_delivery_write);
        toolbarinit();

        categoryTextView = (TextView) findViewById(R.id.delivery_write_category);

        findViewById(R.id.delivery_write_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory();
            }
        });

        findViewById(R.id.board_write_photo_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPictureData();
            }
        });
        ApplicationUtil.getInstance().setTypeFace(getWindow().getDecorView());
    }

    private void selectCategory() {
        final String[] items = getResources().getStringArray(R.array.delivery_list);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.text_select_delivery_category))
                .setSingleChoiceItems(items, selectedCategoryItem,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                selectedCategoryItem = whichButton;
                            }
                        }).setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // OK 버튼 클릭시 , 여기서 선택한 값을 메인 Activity 로 넘기면 된다.
                                categoryTextView.setText(items[selectedCategoryItem]);
                            }
                        })
                .setCancelable(false)
                .show();
        ApplicationUtil.getInstance().setTypeFace(alertDialog.getWindow().getDecorView());
    }

    private void toolbarinit() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClickWrite(View view) {
        if (isValidData()) {
            writeBoard();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_PICTURE_RESULT_CODE && resultCode == RESULT_OK) {
            addBoardPhotodata(data.getData());
        }
    }

    private void addBoardPhotodata(Uri uri) {
        fileListMap.clear();
        if (fileListMap.get(uri.toString()) != null) {
            /* 이미 파일이 목록에 있을때 return */
            return;
        }
        fileListMap.put(uri.toString(), uri);
        final LinearLayout photoDataView = (LinearLayout) findViewById(R.id.board_write_photo_data);
        final View photoChildView = LayoutInflater.from(this).inflate(R.layout.ui_boardwrite_photo, photoDataView, false);

        /* 사진 한장 사용 해요 */
        photoDataView.removeAllViews();

        /* 사진 추가해요 */
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
        invalidateScrollView();
    }

    private void writeBoard() {
        new AsyncTask<Void, Void, JSONObject>() {
            private ClearProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                if (!ApplicationUtil.getInstance().isOnlineNetwork()) {
                    AlertToast.error(getApplicationContext(), R.string.error_check_network_state);
                    cancel(true);
                    return;
                }
                dialog = new ClearProgressDialog(DeliveryFoodWriteActivity.this);
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject result = null;
                try {
                    TextView shopNameView = (TextView) findViewById(R.id.delivery_write_shop_name);
                    EditText phonenumberView = (EditText) findViewById(R.id.delivery_write_phonenumber);

                    result = NetworkUtil.getInstance().checkIsLoginUser().writeDelivery(
                            selectedCategoryItem + 1, shopNameView.getText().toString(),
                            phonenumberView.getText().toString(), fileListMap);
                } catch (IOException | ParseException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                dialog.cancel();

                if (jsonObject == null) {
                    AlertToast.error(getApplicationContext(), R.string.error_to_work);
                    return;
                }
                String result = jsonObject.get("result").toString();
                if (result.equals("success")) {
                    setResult(RESULT_OK);
                    finish();
                }
                if (result.equals("fail")) {
                    String reason = jsonObject.get("reason").toString();
                    if (reason.equals("emptyuserinfo")) {
                        AlertToast.error(getApplicationContext(), R.string.error_empty_studentnumber_info);
                        UserData.getInstance().logoutUser();
                    } else {
                        AlertToast.error(getApplicationContext(), R.string.error_board_write);
                    }
                }
            }
        }.execute();
    }

    private boolean isValidData() {
        TextView categoryView = (TextView) findViewById(R.id.delivery_write_category);
        TextView shopNameView = (TextView) findViewById(R.id.delivery_write_shop_name);
        EditText phonenumberView = (EditText) findViewById(R.id.delivery_write_phonenumber);

        if (categoryView.getText().toString().isEmpty()) {
            AlertToast.warning(getApplicationContext(), "배달음식 카테고리를 선택해주세요");
            return false;
        } else if (shopNameView.getText().toString().isEmpty()) {
            AlertToast.warning(getApplicationContext(), "음식점 이름을 입력해주세요");
            return false;
        } else if (phonenumberView.getText().toString().isEmpty()) {
            AlertToast.warning(getApplicationContext(), "음식점 번호를 입력해주세요");
            return false;
        } else if (fileListMap.size() == 0) {
            AlertToast.warning(getApplicationContext(), "음식점 이미지를 넣어주세요");
            return false;
        } else {
            return true;
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

    public void getPictureData() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_PICTURE_RESULT_CODE);
    }
}
