package com.htt.kon.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.bean.Music;
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

    public static Notification of(String style, Context context, Music music) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.notification_play_1);
        Intent intent = new Intent();
        intent.setAction(PlayNotificationReceiver.ACTION);
        PendingIntent intent1 = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.textView, intent1);

        if (music != null) {
            view.setImageViewBitmap(R.id.np1_imageView, BitmapFactory.decodeFile(music.getImage()));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.N_C_PLAY_ID);
        return builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(view)
                .setCustomBigContentView(view)
                .setWhen(System.currentTimeMillis())
                .build();
    }

}
