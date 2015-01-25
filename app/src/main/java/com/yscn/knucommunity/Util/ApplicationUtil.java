package com.yscn.knucommunity.Util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.TypedValue;

/**
 * Created by GwonHyeok on 2015. 1. 23..
 */
public class ApplicationUtil {

    public static ApplicationUtil instance;

    private ApplicationUtil() {
    }

    public synchronized static ApplicationUtil getInstance() {
        if (instance == null) {
            instance = new ApplicationUtil();
        }
        return instance;
    }

    public String UriToPath(Uri uri) {
        Context context = ApplicationContextProvider.getContext();

        if (isGooglePhotoUri(uri)) {
            return uri.getLastPathSegment();
        }

        if (Build.VERSION.SDK_INT >= 19) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;

                    switch (type) {
                        case "image":
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "video":
                            return null;
                        case "audio":
                            return null;
                    }

                    final String selection = MediaStore.Images.Media._ID + "=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }

        // content://media/external/images/media/....
        // 안드로이드 버전에 관계없이 경로가 com.android... 형식으로 집히지 않을 수 도 있음. [ 겔럭시S4 테스트 확인 ]
        if (isPathSDCardType(uri)) {

            final String selection = MediaStore.Images.Media._ID + "=?";
            final String[] selectionArgs = new String[]{
                    uri.getLastPathSegment()
            };

            return getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
        }
        // File 접근일 경우
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isGooglePhotoUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private boolean isPathSDCardType(Uri uri) {
        return "external".equals(uri.getPathSegments().get(0));
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                ApplicationContextProvider.getContext().getResources().getDisplayMetrics());
    }
}
