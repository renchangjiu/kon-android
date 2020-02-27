package com.htt.kon.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import java.io.Serializable;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/12 19:39
 */
public class PlayStateChangeReceiver extends BroadcastReceiver {

    public static final String ACTION = "COM.HTT.KON.MUSIC.PLAY.STATE.RECEIVER";


    @Setter
    private OnReceiveListener onReceiveListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Serializable value = intent.getSerializableExtra("key");
        Optional.of(this.onReceiveListener).ifPresent(v -> v.onReceive((Flag) value));
        LogUtils.e("MusicPlayStateReceiver Receive a broadcast.");
    }

    /**
     * 发出广播
     */
    public static void send(Context context, Flag flag) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("key", flag);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        LogUtils.e("Send a broadcast, Action: " + PlayStateChangeReceiver.ACTION);
    }

    public interface OnReceiveListener {
        void onReceive(Flag flag);
    }

    public enum Flag {
        /**
         * 表示歌曲开始播放
         */
        PLAY,

        /**
         * 表示播放列表被清空
         */
        CLEAR,

        /**
         * 表示播放列表被删除了一项
         */
        REMOVE,
    }
}
