package com.htt.kon.fragment.music;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.R;
import com.htt.kon.activity.MusicsActivity;
import com.htt.kon.adapter.list.music.ArtistAdapter;
import com.htt.kon.adapter.list.music.ItemData;
import com.htt.kon.bean.Music;

import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.LogUtils;

import java.util.List;

/**
 * 本地音乐activity 下的歌手页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class ArtistPagerFragment extends BaseLocalMusicPagerFragment {

    private ArtistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.init();
        return view;
    }


    private void init() {
        this.adapter = new ArtistAdapter(this.activity);
        super.listView.setAdapter(adapter);

        super.listView.setOnItemClickListener((parent, view, position, id) -> {
            ItemData item = adapter.getItem(position);
            MusicsActivity.start(this.activity, item.getTitle(), item.getMusics());
        });
    }


    private ArtistPagerFragment() {
    }

    public static ArtistPagerFragment of() {
        return new ArtistPagerFragment();
    }
}
