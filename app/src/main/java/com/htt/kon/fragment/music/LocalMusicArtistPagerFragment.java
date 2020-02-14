package com.htt.kon.fragment.music;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.LocalMusicSingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.MusicPlayStateBroadcastReceiver;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.UiUtils;

/**
 * 本地音乐activity 下的歌手tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class LocalMusicArtistPagerFragment extends Fragment {

    private LocalMusicActivity activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music_artist, container, false);
        // this.listView = view.findViewById(R.id.flms_listView);
        //
        // this.musicDbService = MusicDbService.of(this.activity);
        // this.init();
        //
        // this.receiver = MusicPlayStateBroadcastReceiver.register(this.activity);
        // this.receiver.setOnReceiveBroadcastListener(() -> {
        //     Music curMusic = this.playlist.getCurMusic();
        //     if (curMusic.getMid().equals(MidConstant.MID_LOCAL_MUSIC)) {
        //         UiUtils.getListViewAdapter(this.listView, LocalMusicSingleAdapter.class).notifyDataSetChanged();
        //     }
        // });
        return view;
    }


    private LocalMusicArtistPagerFragment() {
    }

    public static LocalMusicArtistPagerFragment of() {
        return new LocalMusicArtistPagerFragment();
    }
}
