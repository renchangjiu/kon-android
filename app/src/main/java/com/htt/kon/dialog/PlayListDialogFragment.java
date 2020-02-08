package com.htt.kon.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.adapter.list.dialog.PlaylistDialogAdapter;
import com.htt.kon.bean.Playlist;
import com.htt.kon.service.MusicService;
import com.htt.kon.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

/**
 * @author su
 * @date 2020/02/08 17:48
 */
public class PlayListDialogFragment extends BaseDialogFragment {

    @BindView(R.id.dp_textViewPlayMode)
    TextView textViewPlayMode;

    @BindView(R.id.dp_textViewCollect)
    TextView textViewCollect;

    @BindView(R.id.dp_imageViewClear)
    ImageView imageViewClear;

    @BindView(R.id.dp_listView)
    ListView listView;

    @Setter
    private OnSelectListener onSelectListener;

    private MusicService msService;
    private App app;
    private Playlist playlist;

    public static PlayListDialogFragment of(MusicService msService) {
        PlayListDialogFragment instance = new PlayListDialogFragment();
        instance.msService = msService;
        instance.app = App.getApp();
        instance.playlist = instance.app.getPlaylist();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_playlist, container, false);
        ButterKnife.bind(this, view);

        this.init();
        return view;
    }

    private void init() {
        this.listView.setAdapter(new PlaylistDialogAdapter(this.playlist.getIndex()));
        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            // 更新点击项图标
            PlaylistDialogAdapter adapter = (PlaylistDialogAdapter) this.listView.getAdapter();
            adapter.setClickPos(position);
            msService.play(position);
        });
    }


    public interface OnSelectListener {
        void onSelect(int tag);

    }
}
