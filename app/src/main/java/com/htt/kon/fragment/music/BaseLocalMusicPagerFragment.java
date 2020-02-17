package com.htt.kon.fragment.music;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.LocalMusicSingleAdapter;
import com.htt.kon.broadcast.MusicPlayStateBroadcastReceiver;
import com.htt.kon.service.Playlist;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.UiUtils;

/**
 * @author su
 * @date 2020/02/16 14:00
 */
public abstract class BaseLocalMusicPagerFragment extends Fragment {

    ListView listView;

    LocalMusicActivity activity;

    Playlist playlist;

    MusicDbService musicDbService;

    private MusicPlayStateBroadcastReceiver receiver;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music_abd, container, false);
        this.listView = view.findViewById(R.id.flm_listView);
        this.init();
        return view;
    }

    private void init() {
        this.receiver = MusicPlayStateBroadcastReceiver.register(this.activity);
        this.receiver.setOnReceiveBroadcastListener(this::onReceiveBroadcast);
        this.musicDbService = MusicDbService.of(this.activity);
        this.playlist = App.getApp().getPlaylist();
    }

    /**
     * 当接收到广播时调用. 除单曲页面外, 其他三个页面无需重写此方法
     *
     * @param flag MusicPlayStateBroadcastReceiver.FLAG_PLAY or MusicPlayStateBroadcastReceiver.FLAG_CLEAR, etc
     */
    public void onReceiveBroadcast(int flag) {
        switch (flag) {
            case MusicPlayStateBroadcastReceiver.FLAG_PLAY:
            case MusicPlayStateBroadcastReceiver.FLAG_CLEAR:
            case MusicPlayStateBroadcastReceiver.FLAG_REMOVE:
                UiUtils.getListViewAdapter(this.listView, BaseAdapter.class).notifyDataSetChanged();
                break;
            default:
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayStateBroadcastReceiver.unregister(this.activity, this.receiver);
    }

}
