package com.htt.kon.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import lombok.Setter;

/**
 * 播放的通知栏的广播消息
 *
 * @author su
 * @date 2020/02/24 21:56
 */
public class PlayNotificationReceiver extends BroadcastReceiver {

    public static final String ACTION = "COM.HTT.KON.MUSIC.PLAY.NOTIFICATION.RECEIVER";

    @Setter
    private OnReceiveListener onReceiveListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("todo");
        Optional.of(this.onReceiveListener).ifPresent(OnReceiveListener::onReceive);
    }


    public interface OnReceiveListener {
        void onReceive();
    }
}
