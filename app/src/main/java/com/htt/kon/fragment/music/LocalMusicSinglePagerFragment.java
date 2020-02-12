package com.htt.kon.fragment.music;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.LocalMusicSingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.MusicPlayStateBroadcastReceiver;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.service.Playlist;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.UiUtils;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本地音乐activity 下的单曲tab 页
 *
 * @author su
 * @date 2020/02/03 21:00
 */
public class LocalMusicSinglePagerFragment extends Fragment {


    @BindView(R.id.lhs_textViewCount)
    TextView textViewCount;

    @BindView(R.id.lhs_textViewMultipleChoice)
    TextView textViewMultipleChoice;

    @BindView(R.id.flms_listView)
    ListView listView;


    private MusicDbService musicDbService;

    private LocalMusicActivity activity;

    private MusicPlayStateBroadcastReceiver receiver;
    private Playlist playlist;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music_single, container, false);
        this.listView = view.findViewById(R.id.flms_listView);

        this.musicDbService = MusicDbService.of(this.activity);
        this.init();

        this.receiver = MusicPlayStateBroadcastReceiver.register(this.activity);
        this.receiver.setOnReceiveBroadcastListener(() -> {
            Music curMusic = this.playlist.getCurMusic();
            if (curMusic.getMid().equals(MidConstant.MID_LOCAL_MUSIC)) {
                UiUtils.getListViewAdapter(this.listView, LocalMusicSingleAdapter.class).notifyDataSetChanged();
            }
        });
        return view;
    }


    private void init() {
        this.playlist = App.getApp().getPlaylist();
        View headerView = LayoutInflater.from(this.activity).inflate(R.layout.list_header_single, null);
        this.listView.addHeaderView(headerView);
        ButterKnife.bind(this, this.listView);

        new Thread(() -> {
            List<Music> list = this.musicDbService.list(MidConstant.MID_LOCAL_MUSIC);
            this.activity.runOnUiThread(() -> {
                this.listView.setAdapter(new LocalMusicSingleAdapter(list, this.activity));
                String format = this.activity.getString(R.string.local_music_count);
                this.textViewCount.setText(String.format(format, list.size()));
            });
        }).start();

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            setPlaylist(position - 1);
        });

        // 播放全部
        headerView.setOnClickListener(v -> {
            setPlaylist(0);
        });

        this.textViewMultipleChoice.setOnClickListener(v -> {
            LogUtils.e("textViewMultipleChoice");
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayStateBroadcastReceiver.unregister(this.activity, this.receiver);
    }

    /**
     * 更新播放列表
     *
     * @param index 在新的播放列表中的index
     */
    private void setPlaylist(int index) {
        // 使本地音乐列表成为新的播放列表
        new Thread(() -> {
            List<Music> list = this.musicDbService.list(MidConstant.MID_LOCAL_MUSIC);
            this.activity.runOnUiThread(() -> {
                this.activity.replacePlaylist(list, index);
                // 通知adapter 更改界面
                UiUtils.getListViewAdapter(this.listView, LocalMusicSingleAdapter.class).notifyDataSetChanged();
            });
        }).start();
    }

    private LocalMusicSinglePagerFragment() {
        super();
    }

    public static LocalMusicSinglePagerFragment of() {
        return new LocalMusicSinglePagerFragment();
    }


}
