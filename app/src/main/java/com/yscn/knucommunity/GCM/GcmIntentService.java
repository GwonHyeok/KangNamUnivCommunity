package com.yscn.knucommunity.GCM;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yscn.knucommunity.Activity.ShareTaxiDetailActivity;
import com.yscn.knucommunity.Activity.Splash;
import com.yscn.knucommunity.Activity.StudentNotificationActivity;
import com.yscn.knucommunity.R;
import com.yscn.knucommunity.Util.ImageLoaderUtil;
import com.yscn.knucommunity.Util.UserData;

/**
 * Created by GwonHyeok on 15. 1. 17..
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        Log.i(getClass().getSimpleName(), "Received: GCM ");

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " +
//                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras);
                Log.i(getClass().getSimpleName(), "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle extra) {
        boolean isNotificationOn = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("setting_preference_notification", true);
        /* 만약 설정에서 알림을 꺼놓으면 리턴 */
        if (!isNotificationOn) {
            return;
        }

        long vib_pattern[] = {100, 500};

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Splash.class), 0);

        String title = extra.getString("title");
        String message = extra.getString("msg");
        String photo_url = extra.getString("photo");
        String type = extra.getString("type") != null ? extra.getString("type") : "";
        int NOTIFICATION_ID = Integer.parseInt(extra.getString("notification_id"));

        if (type.equals("BOARD_NOTIFY")) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getString(R.string.receive_new_board_comment)))
                            .setAutoCancel(true)
                            .setVibrate(vib_pattern)
                            .setContentIntent(PendingIntent.getActivity(this, 0,
                                    new Intent(this, StudentNotificationActivity.class), 0))
                            .setContentText(getString(R.string.receive_new_board_comment));
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } else if (type.equals("TAXI_NOTIFY")) {
            String contentid = extra.getString("contentid");
            Intent intent = new Intent(this, ShareTaxiDetailActivity.class);
            intent.putExtra("contentID", contentid);
            intent.putExtra("writerStudentNumber", UserData.getInstance().getStudentNumber());
            intent.putExtra("isFromNotify", false);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setAutoCancel(true)
                            .setVibrate(vib_pattern)
                            .setContentIntent(pendingIntent)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getString(R.string.receive_new_taxi_action)))
                            .setContentText(getString(R.string.receive_new_taxi_action))
                            .setContentTitle(getString(R.string.app_name));
            mNotificationManager.notify(0x13, mBuilder.build());
        } else {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(title != null ? title : getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setVibrate(vib_pattern)
                            .setAutoCancel(true)
                            .setContentText(message);

            if (!photo_url.isEmpty()) {
                ImageLoaderUtil.getInstance().initImageLoader();
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(photo_url);
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setSummaryText(message)
                        .setBigContentTitle(title));
            }
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}
