package com.htt.kon.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import java.io.Serializable;

import lombok.Setter;

/**
 * 播放的通知栏的广播消息
 *
 * @author su
 * @date 2020/02/24 21:56
 */
public class PlayNotificationReceiver extends BroadcastReceiver implements BaseReceiver {

    public static final String ACTION = "COM.HTT.KON.MUSIC.PLAY.NOTIFICATION.RECEIVER";

    @Setter
    private OnReceiveListener onReceiveListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Serializable value = intent.getSerializableExtra("key");
        Optional.of(this.onReceiveListener).ifPresent(v -> v.onReceive((Flag) value));
        LogUtils.e("PlayNotificationReceiver Receive a broadcast.");
    }

    public static Intent getIntent(Flag flag) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("key", flag);
        return intent;
    }


    public interface OnReceiveListener {
        void onReceive(Flag flag);
    }

    public enum Flag {
        /**
         * 表示点击关闭按钮
         */
        CLOSE,

        /**
         * 点击爱心按钮
         */
        LIKE,

        /**
         * 点击上一首按钮
         */
        PREV,

        /**
         * 点击播放按钮
         */
        PLAY,

        /**
         * 点击下一首按钮
         */
        NEXT,

        /**
         * 点击歌词按钮
         */
        LRC
    }
}
