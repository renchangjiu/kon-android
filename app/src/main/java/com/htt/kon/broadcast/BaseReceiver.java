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
 * @date 2020/02/25 08:37
 */
public interface BaseReceiver {

    static void register(Context context, BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        context.registerReceiver(receiver, filter);
        LogUtils.e(receiver.getClass().getSimpleName() + " register.");
    }

    static void registerLocal(Context context, BroadcastReceiver receiver, String... actions) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        manager.registerReceiver(receiver, filter);
    }


    static void unregister(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    static void unregisterLocal(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.unregisterReceiver(receiver);
    }

    /**
     * 发出广播
     */
    static void send(Context context, Intent intent) {
        context.sendBroadcast(intent);
    }

    /**
     * 发出广播
     */
    static void sendLocal(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
