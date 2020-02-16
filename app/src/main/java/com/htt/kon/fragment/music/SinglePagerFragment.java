package com.htt.kon.fragment.music;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.LocalMusicSingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.MusicPlayStateBroadcastReceiver;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.dialog.CommonDialogFragment;
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
public class SinglePagerFragment extends BaseLocalMusicPagerFragment {


    @BindView(R.id.lhs_textViewCount)
    TextView textViewCount;

    @BindView(R.id.lhs_textViewMultipleChoice)
    TextView textViewMultipleChoice;

    @BindView(R.id.flms_listView)
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_local_music_single, container, false);
        this.listView = view.findViewById(R.id.flms_listView);

        this.init();
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
                LocalMusicSingleAdapter adapter = new LocalMusicSingleAdapter(list, this.activity);
                this.listView.setAdapter(adapter);
                String format = this.activity.getString(R.string.local_music_count);
                this.textViewCount.setText(String.format(format, list.size()));

                adapter.setOnOptionClickListener(item -> {
                    Music data = (Music) item.getData();
                    switch (item.getId()) {
                        case CommonDialogFragment.TAG_PLAY_NEXT:
                            this.activity.nextPlay(data);
                            Toast.makeText(this.activity, this.activity.getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                    }
                    LogUtils.e(item);
                });
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
    public void onReceiveBroadcast(int flag) {
        switch (flag) {
            case MusicPlayStateBroadcastReceiver.FLAG_PLAY:
            case MusicPlayStateBroadcastReceiver.FLAG_CLEAR:
            case MusicPlayStateBroadcastReceiver.FLAG_REMOVE:
                UiUtils.getListViewAdapter(this.listView, LocalMusicSingleAdapter.class).notifyDataSetChanged();
                break;
            default:
        }
    }

    /**
     * 更新播放列表
     *
     * @param index 在新的播放列表中的index
     */
    private void setPlaylist(int index) {
        // 使本地音乐列表替代成为新的播放列表
        new Thread(() -> {
            List<Music> list = this.musicDbService.list(MidConstant.MID_LOCAL_MUSIC);
            this.activity.runOnUiThread(() -> {
                this.activity.replacePlaylist(list, index);
                // 通知adapter 更改界面
                UiUtils.getListViewAdapter(this.listView, LocalMusicSingleAdapter.class).notifyDataSetChanged();
            });
        }).start();
    }

    private SinglePagerFragment() {
        super();
    }

    public static SinglePagerFragment of() {
        return new SinglePagerFragment();
    }


}
