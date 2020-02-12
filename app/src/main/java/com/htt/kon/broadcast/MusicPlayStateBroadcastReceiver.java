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

    @Setter
    private OnReceiveBroadcastListener onReceiveBroadcastListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("MusicPlayStateBroadcastReceiver Receive a broadcast.");
        Optional.of(this.onReceiveBroadcastListener).ifPresent(OnReceiveBroadcastListener::onReceiveBroadcast);
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

    public interface OnReceiveBroadcastListener {
        /**
         * 当接收到广播时调用
         */
        void onReceiveBroadcast();
    }
}
