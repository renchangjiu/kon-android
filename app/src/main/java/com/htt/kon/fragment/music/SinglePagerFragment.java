package com.htt.kon.fragment.music;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.R;
import com.htt.kon.activity.MusicsCheckActivity;
import com.htt.kon.adapter.list.music.SingleAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本地音乐activity 下的单曲页
 *
 * @author su
 * @date 2020/02/03 21:00
 */
public class SinglePagerFragment extends BaseLocalMusicPagerFragment {

    private Handler handler = new Handler((msg) -> {
        if (musics.isEmpty()) {
            this.listView.removeHeaderView(this.headerView);
            return true;
        }
        String format = this.activity.getString(R.string.local_music_count);
        this.textViewCount.setText(String.format(format, musics.size()));
        return true;
    });

    @BindView(R.id.lhs_textViewCount)
    TextView textViewCount;

    @BindView(R.id.lhs_textViewMultipleChoice)
    TextView textViewMultipleChoice;

    private SingleAdapter adapter;

    private View headerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.init();
        return view;
    }


    private void init() {
        headerView = LayoutInflater.from(this.activity).inflate(R.layout.list_header_single, this.listView, false);
        super.listView.addHeaderView(headerView);
        ButterKnife.bind(this, this.listView);

        this.adapter = new SingleAdapter(this.activity);
        super.listView.setAdapter(adapter);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            setPlaylist(position - 1);
        });

        // 播放全部
        headerView.setOnClickListener(v -> {
            setPlaylist(0);
        });

        this.textViewMultipleChoice.setOnClickListener(v -> {
            MusicsCheckActivity.start(this.activity, this.adapter.getRes());
        });
    }

    @Override
    public void dataInitialized() {
        this.handler.sendEmptyMessage(0);
    }

    /**
     * 更新播放列表
     *
     * @param index 在新的播放列表中的index
     */
    private void setPlaylist(int index) {
        // 使本地音乐列表替代成为新的播放列表
        this.activity.replacePlaylist(this.musics, index);
        this.adapter.notifyDataSetChanged();
    }

    private SinglePagerFragment() {
        super();
    }

    public static SinglePagerFragment of() {
        return new SinglePagerFragment();
    }


}
