package com.htt.kon.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.broadcast.PlayNotificationReceiver;

/**
 * @author su
 * @date 2020/02/24 22:01
 */
public class PlayNotification {

    /**
     * 样式一
     */
    public static final String STYLE_1 = "1";

    public static Notification of(String style, Context context) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.notification_play);
        Intent intent = new Intent(context, PlayNotificationReceiver.class);
        intent.setAction(PlayNotificationReceiver.ACTION);
        PendingIntent intent1 = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.textView, intent1);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.N_C_PLAY_ID);
        return builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(view)
                .setCustomBigContentView(view)
                .setWhen(System.currentTimeMillis())
                .build();
    }

}
