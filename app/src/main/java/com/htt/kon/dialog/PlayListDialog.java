package com.htt.kon.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.BaseActivity;
import com.htt.kon.adapter.list.dialog.PlaylistDialogAdapter;
import com.htt.kon.bean.PlayMode;
import com.htt.kon.broadcast.BaseReceiver;
import com.htt.kon.broadcast.PlayStateChangeReceiver;
import com.htt.kon.service.MusicService;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.UiUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2020/02/08 17:48
 */
public class PlayListDialog extends DialogFragment {

    @BindView(R.id.dp_textViewPlayMode)
    TextView textViewPlayMode;

    @BindView(R.id.dp_textViewCollect)
    TextView textViewCollect;

    @BindView(R.id.dp_imageViewClear)
    ImageView imageViewClear;

    @BindView(R.id.dp_listView)
    ListView listView;

    private Context context;

    private Playlist playlist;

    private PlaylistDialogAdapter adapter;

    private MusicService msService;

    private PlayStateChangeReceiver receiver;

    private PlayListDialog() {
        super();
    }

    public static PlayListDialog of(MusicService msService) {
        PlayListDialog res = new PlayListDialog();
        res.msService = msService;
        return res;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置dialog: 背景透明、宽度为屏宽、位置在屏幕底部
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.6);
        window.setAttributes(params);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.playlist = App.getPlaylist();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_playlist, container, false);
        ButterKnife.bind(this, view);

        this.init();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BaseReceiver.unregisterLocal(context, this.receiver);
    }

    private void init() {
        this.updateModeInterface();
        this.adapter = new PlaylistDialogAdapter();
        this.listView.setAdapter(adapter);
        this.listView.setSelection(this.playlist.getIndex());
        this.adapter.setOnClickListener(new PlaylistDialogAdapter.OnClickListener() {
            @Override
            public void onLocateBtnClick(int position) {
                // TODO
            }

            @Override
            public void onDeleteBtnClick(int position) {
                msService.remove(position);
                adapter.notifyDataSetChanged();
                updateModeInterface();
                if (adapter.getCount() == 0) {
                    dismiss();
                }
            }
        });

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position != playlist.getIndex() || !msService.isPlaying()) {
                msService.play(position);
            }
        });

        this.receiver = PlayStateChangeReceiver.registerLocal(context, flag -> {
            this.adapter.notifyDataSetChanged();
        });
    }

    @OnClick({R.id.dp_textViewPlayMode, R.id.dp_textViewCollect, R.id.dp_imageViewClear})
    void click(View view) {
        switch (view.getId()) {
            // 播放模式按钮的点击事件
            case R.id.dp_textViewPlayMode:
                msService.setMode();
                this.updateModeInterface();
                break;
            // 收藏按钮的点击事件
            // TODO
            case R.id.dp_textViewCollect:
                break;
            //  清空按钮的点击事件
            case R.id.dp_imageViewClear:
                OptionDialog.of(context).setContent(getString(R.string.sure_to_clear_playlist))
                        .setPositiveButton(getString(R.string.clear), (child) -> {
                            msService.clear();
                            this.adapter.notifyDataSetChanged();
                            dismiss();
                        })
                        .setNegativeButton(child -> {
                        })
                        .show();
                break;
            default:
        }
    }

    /**
     * 设置播放模式按钮的文字和图片
     */
    private void updateModeInterface() {
        int mode = this.playlist.getMode();
        PlayMode playMode = Playlist.getModeByValue(mode, this.context);
        int imageId;
        switch (mode) {
            case Playlist.MODE_LOOP:
                imageId = R.drawable.playlist_loop_play;
                break;
            case Playlist.MODE_RANDOM:
                imageId = R.drawable.playlist_random_play;
                break;
            default:
                imageId = R.drawable.playlist_single_loop_play;
                break;
        }
        Drawable drawable = this.context.getResources().getDrawable(imageId, null);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        this.textViewPlayMode.setCompoundDrawables(drawable, null, null, null);
        String format = getString(R.string.play_mode_show);
        this.textViewPlayMode.setText(String.format(format, playMode.getLabel(), this.playlist.size()));
    }

}
