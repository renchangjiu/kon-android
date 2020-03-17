package com.htt.kon.fragment.music;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.MusicsCheckedActivity;
import com.htt.kon.adapter.list.music.SingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.broadcast.PlayStateChangeReceiver;

import com.htt.kon.constant.CommonConstant;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.util.JsonUtils;
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

    private ListView listView;
    private SingleAdapter adapter;

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
        this.playlist = App.getPlaylist();
        View headerView = LayoutInflater.from(this.activity).inflate(R.layout.list_header_single, this.listView, false);
        this.listView.addHeaderView(headerView);
        ButterKnife.bind(this, this.listView);

        this.adapter = new SingleAdapter(this.activity);
        this.listView.setAdapter(adapter);

        this.musicDbService.list(CommonConstant.MID_LOCAL_MUSIC, musics -> {
            this.activity.runOnUiThread(() -> {
                String format = this.activity.getString(R.string.local_music_count);
                this.textViewCount.setText(String.format(format, musics.size()));
            });
        });
        adapter.setOnOptionClickListener(item -> {
            Music music = JsonUtils.json2Bean(item.getData(), Music.class);
            switch (item.getId()) {
                case CommonDialog.TAG_PLAY_NEXT:
                    this.activity.nextPlay(music);
                    Toast.makeText(this.activity, this.activity.getString(R.string.added_to_next_play), Toast.LENGTH_SHORT).show();
                    break;
                case CommonDialog.TAG_COLLECT:
                    // 收藏到歌单
                    MusicListDialog mlDialog = MusicListDialog.of(music, music.getTitle());
                    mlDialog.show(activity.getSupportFragmentManager(), "1");
                    break;
                default:
            }
        });
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            setPlaylist(position - 1);
        });


        // 播放全部
        headerView.setOnClickListener(v -> {
            setPlaylist(0);
        });

        this.textViewMultipleChoice.setOnClickListener(v -> {
            Intent intent = new Intent(this.activity, MusicsCheckedActivity.class);
            intent.putExtras(MusicsCheckedActivity.putData(this.adapter.getRes()));
            startActivity(intent);
        });
    }


    @Override
    public void onReceiveBroadcast(PlayStateChangeReceiver.Flag flag) {
        switch (flag) {
            case PLAY:
            case CLEAR:
            case REMOVE:
                UiUtils.getAdapter(this.listView, SingleAdapter.class).notifyDataSetChanged();
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
        App.getPoolExecutor().execute(() -> {
            List<Music> list = this.musicDbService.list(CommonConstant.MID_LOCAL_MUSIC);
            this.activity.runOnUiThread(() -> {
                this.activity.replacePlaylist(list, index);
                // 通知adapter 更改界面
                UiUtils.getAdapter(this.listView, SingleAdapter.class).notifyDataSetChanged();
            });
        });
    }

    private SinglePagerFragment() {
        super();
    }

    public static SinglePagerFragment of() {
        return new SinglePagerFragment();
    }


}
