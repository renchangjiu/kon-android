package com.htt.kon.fragment.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.R;
import com.htt.kon.activity.MusicsActivity;
import com.htt.kon.adapter.list.music.AlbumAdapter;
import com.htt.kon.adapter.list.music.ItemData;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.dialog.MusicListDialog;
import com.htt.kon.util.JsonUtils;

import java.util.List;

/**
 * 本地音乐activity 下的专辑tab 页
 *
 * @author su
 * @date 2020/02/14 21:44
 */
public class AlbumPagerFragment extends BaseLocalMusicPagerFragment {


    private AlbumAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.init();
        return view;
    }


    private void init() {
        this.adapter = new AlbumAdapter(this.activity);
        this.listView.setAdapter(adapter);

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            ItemData item = this.adapter.getItem(position);
            MusicsActivity.start(this.activity, item.getTitle(), item.getMusics());
        });
    }


    private AlbumPagerFragment() {
    }

    public static AlbumPagerFragment of() {
        return new AlbumPagerFragment();
    }
}
