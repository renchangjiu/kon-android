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
import com.htt.kon.activity.MusicPlayActivity;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.PlayNotificationReceiver;

import org.apache.commons.lang3.StringUtils;

/**
 * @author su
 * @date 2020/02/24 22:01
 */
public class PlayNotification {

    public static Notification of(Style style, Context context, Music music, boolean isPlaying) {
        switch (style) {
            case ONE:
                return of(new RemoteViews(context.getPackageName(), R.layout.notification_play_1_big), context, music, isPlaying);
            case TWO:
            default:
                return of(new RemoteViews(context.getPackageName(), R.layout.notification_play_2_big), context, music, isPlaying);
        }
    }

    private static Notification of(RemoteViews view, Context context, Music music, boolean isPlaying) {
        setPendingIntent(view, context);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_play);

        Intent intent1 = PlayNotificationReceiver.getIntent(PlayNotificationReceiver.Flag.CLOSE);
        contentView.setOnClickPendingIntent(R.id.np_imageViewClose, PendingIntent.getBroadcast(context, PlayNotificationReceiver.Flag.CLOSE.ordinal(), intent1, PendingIntent.FLAG_UPDATE_CURRENT));

        initViewData(view, contentView, context, music, isPlaying);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.N_C_PLAY_ID);
        return builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(contentView)
                .setCustomBigContentView(view)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(context, 1, new Intent(context, MusicPlayActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
    }

    /**
     * 设置 view 显示的数据
     */
    private static void initViewData(RemoteViews view, RemoteViews contentView, Context context, Music music, boolean isPlaying) {
        if (music == null) {
            return;
        }
        if (StringUtils.isNotEmpty(music.getImage())) {
            view.setImageViewBitmap(R.id.np_imageView, BitmapFactory.decodeFile(music.getImage()));
            contentView.setImageViewBitmap(R.id.np_imageView, BitmapFactory.decodeFile(music.getImage()));
        } else {
            view.setImageViewResource(R.id.np_imageView, R.drawable.playbar_cover_image_default);
            contentView.setImageViewResource(R.id.np_imageView, R.drawable.playbar_cover_image_default);
        }

        view.setImageViewResource(R.id.np_imageViewPlay, isPlaying ? R.drawable.note_btn_play_white : R.drawable.note_btn_pause_white);
        contentView.setImageViewResource(R.id.np_imageViewPlay, isPlaying ? R.drawable.note_btn_play_white : R.drawable.note_btn_pause_white);

        view.setTextViewText(R.id.np_textViewTitle, music.getTitle());
        contentView.setTextViewText(R.id.np_textViewTitle, music.getTitle());

        String format = context.getString(R.string.artist_album);
        view.setTextViewText(R.id.np_textViewArtist, String.format(format, music.getArtist(), music.getAlbum()));
        contentView.setTextViewText(R.id.np_textViewArtist, String.format(format, music.getArtist(), music.getAlbum()));
    }


    private static void setPendingIntent(RemoteViews view, Context context) {
        Intent intent1 = PlayNotificationReceiver.getIntent(PlayNotificationReceiver.Flag.CLOSE);
        view.setOnClickPendingIntent(R.id.np_imageViewClose, PendingIntent.getBroadcast(context, PlayNotificationReceiver.Flag.CLOSE.ordinal(), intent1, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent intent2 = PlayNotificationReceiver.getIntent(PlayNotificationReceiver.Flag.LIKE);
        view.setOnClickPendingIntent(R.id.np_imageViewLike, PendingIntent.getBroadcast(context, PlayNotificationReceiver.Flag.LIKE.ordinal(), intent2, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent intent3 = PlayNotificationReceiver.getIntent(PlayNotificationReceiver.Flag.PREV);
        view.setOnClickPendingIntent(R.id.np_imageViewPrev, PendingIntent.getBroadcast(context, PlayNotificationReceiver.Flag.PREV.ordinal(), intent3, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent intent4 = PlayNotificationReceiver.getIntent(PlayNotificationReceiver.Flag.PLAY);
        view.setOnClickPendingIntent(R.id.np_imageViewPlay, PendingIntent.getBroadcast(context, PlayNotificationReceiver.Flag.PLAY.ordinal(), intent4, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent intent5 = PlayNotificationReceiver.getIntent(PlayNotificationReceiver.Flag.NEXT);
        view.setOnClickPendingIntent(R.id.np_imageViewNext, PendingIntent.getBroadcast(context, PlayNotificationReceiver.Flag.NEXT.ordinal(), intent5, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent intent6 = PlayNotificationReceiver.getIntent(PlayNotificationReceiver.Flag.LRC);
        view.setOnClickPendingIntent(R.id.np_imageViewLyc, PendingIntent.getBroadcast(context, PlayNotificationReceiver.Flag.LRC.ordinal(), intent6, PendingIntent.FLAG_UPDATE_CURRENT));
    }


    public enum Style {
        /**
         * 样式一
         */
        ONE,

        /**
         * 样式二
         */
        TWO
    }

}
