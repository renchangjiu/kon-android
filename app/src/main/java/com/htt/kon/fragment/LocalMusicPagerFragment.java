package com.htt.kon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.list.SingleAdapter;
import com.htt.kon.bean.Music;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.service.MusicDbService;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本地音乐activity 下的4个tab页
 *
 * @author su
 * @date 2020/02/03 21:00
 */
public class LocalMusicPagerFragment extends Fragment {
    public static final String FLAG_SINGLE = "single";
    public static final String FLAG_ARTIST = "artist";
    public static final String FLAG_ALBUM = "album";
    public static final String FLAG_DIR = "dir";

    private MusicDbService musicDbService;

    private LocalMusicActivity activity;

    private String flag;

    @BindView(R.id.flm_listView)
    ListView listView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (LocalMusicActivity) context;
        this.flag = getArguments().getString("flag");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        ButterKnife.bind(this, view);
        this.musicDbService = MusicDbService.of(this.activity);
        switch (this.flag) {
            case FLAG_SINGLE:
                this.initListViewSingle();
                break;
            case FLAG_ARTIST:
                break;
            case FLAG_ALBUM:
                break;
            case FLAG_DIR:
                break;
            default:
        }
        return view;
    }


    private void initListViewSingle() {
        View headerView = LayoutInflater.from(this.activity).inflate(R.layout.list_single_header, null);
        this.listView.addHeaderView(headerView);
        new Thread(() -> {
            List<Music> list = this.musicDbService.list(MidConstant.MID_LOCAL_MUSIC);
            this.activity.runOnUiThread(() -> {
                this.listView.setAdapter(new SingleAdapter(list, this.listView, this.activity));
            });
        }).start();
    }

    private LocalMusicPagerFragment() {
        super();
    }

    public static LocalMusicPagerFragment getInstance(String flag) {
        LocalMusicPagerFragment instance = new LocalMusicPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("flag", flag);
        instance.setArguments(bundle);
        return instance;
    }


}
