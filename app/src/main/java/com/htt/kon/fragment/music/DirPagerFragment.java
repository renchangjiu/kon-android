package com.htt.kon.fragment.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.activity.MusicsActivity;
import com.htt.kon.adapter.list.music.DirAdapter;
import com.htt.kon.adapter.list.music.ItemData;

import java.io.File;

/**
 * 本地音乐activity 下的文件夹tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class DirPagerFragment extends BaseLocalMusicPagerFragment {

    private DirAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.init();
        return view;
    }


    private void init() {
        this.adapter = new DirAdapter(this.activity);
        super.listView.setAdapter(adapter);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            ItemData item = this.adapter.getItem(position);
            MusicsActivity.start(this.activity, new File(item.getTitle()).getName(), item.getMusics());
        });
    }

    private DirPagerFragment() {
    }

    public static DirPagerFragment of() {
        return new DirPagerFragment();
    }
}
