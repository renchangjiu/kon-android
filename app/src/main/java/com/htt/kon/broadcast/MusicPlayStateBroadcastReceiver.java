package com.htt.kon.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/12 19:39
 */
public class MusicPlayStateBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION = "COM.HTT.KON.MUSIC.PLAY.STATE.RECEIVER";

    /**
     * 表示歌曲开始播放
     */
    public static final int FLAG_PLAY = 1;

    /**
     * 表示播放列表被清空
     */
    public static final int FLAG_CLEAR = 2;

    /**
     * 表示播放列表被删除了一项
     */
    public static final int FLAG_REMOVE = 3;


    @Setter
    private OnReceiveBroadcastListener onReceiveBroadcastListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Optional.of(this.onReceiveBroadcastListener).ifPresent(v -> v.onReceiveBroadcast(intent.getFlags()));
        LogUtils.e("MusicPlayStateBroadcastReceiver Receive a broadcast.");
    }


    private MusicPlayStateBroadcastReceiver() {
    }


    /**
     * 动态注册本广播接收器
     */
    public static MusicPlayStateBroadcastReceiver register(Context context) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        MusicPlayStateBroadcastReceiver receiver = new MusicPlayStateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        manager.registerReceiver(receiver, intentFilter);
        LogUtils.e("MusicPlayStateBroadcastReceiver register.");
        return receiver;
    }

    /**
     * 务必注销
     */
    public static void unregister(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.unregisterReceiver(receiver);
        LogUtils.e("MusicPlayStateBroadcastReceiver unregister.");
    }

    /**
     * 发出广播
     */
    public static void send(Context context, int flag) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.setFlags(flag);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        LogUtils.e("Send a broadcast, Action: " + MusicPlayStateBroadcastReceiver.ACTION);
    }

    public interface OnReceiveBroadcastListener {
        /**
         * 当接收到广播时调用
         */
        void onReceiveBroadcast(int flag);
    }
}
