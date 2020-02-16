package com.htt.kon.fragment.music;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.App;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.broadcast.MusicPlayStateBroadcastReceiver;
import com.htt.kon.service.Playlist;
import com.htt.kon.service.database.MusicDbService;

/**
 * @author su
 * @date 2020/02/16 14:00
 */
public abstract class BaseLocalMusicPagerFragment extends Fragment {

    public LocalMusicActivity activity;

    private MusicPlayStateBroadcastReceiver receiver;

    public Playlist playlist;

    MusicDbService musicDbService;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        this.init();
        return null;
    }

    private void init() {
        this.receiver = MusicPlayStateBroadcastReceiver.register(this.activity);
        this.receiver.setOnReceiveBroadcastListener(this::onReceiveBroadcast);
        this.musicDbService = MusicDbService.of(this.activity);
        this.playlist = App.getApp().getPlaylist();
    }

    /**
     * 当接收到广播时调用
     *
     * @param flag MusicPlayStateBroadcastReceiver.FLAG_PLAY or MusicPlayStateBroadcastReceiver.FLAG_CLEAR, etc
     */
    public abstract void onReceiveBroadcast(int flag);

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayStateBroadcastReceiver.unregister(this.activity, this.receiver);
    }

}
