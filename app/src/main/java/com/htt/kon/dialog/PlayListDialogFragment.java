package com.htt.kon.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.adapter.list.dialog.PlaylistDialogAdapter;
import com.htt.kon.bean.PlayMode;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.UiUtils;


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
    private OnClickListener onClickListener;

    private Context context;

    private Playlist playlist;

    private PlayListDialogFragment() {
        super();
    }

    public static PlayListDialogFragment of() {
        PlayListDialogFragment instance = new PlayListDialogFragment();
        return instance;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.playlist = App.getApp().getPlaylist();
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
        this.updateModeInterface();
        PlaylistDialogAdapter adapter1 = new PlaylistDialogAdapter();
        this.listView.setAdapter(adapter1);
        adapter1.setOnClickListener(new PlaylistDialogAdapter.OnClickListener() {
            @Override
            public void onLocateBtnClick(int position) {
                assert onClickListener != null;
                onClickListener.onLocateBtnClick(position);
            }

            @Override
            public void onDeleteBtnClick(int position) {
                assert onClickListener != null;
                int i = onClickListener.onDeleteBtnClick(position);
                PlaylistDialogAdapter adapter = UiUtils.getListViewAdapter(listView, PlaylistDialogAdapter.class);
                adapter.notifyDataSetChanged();
                updateModeInterface();
                if (adapter.getCount() == 0) {
                    dismiss();
                }
            }
        });

        this.listView.setOnItemClickListener((parent, view, position, id) -> {
            assert onClickListener != null;
            onClickListener.onItemClick(position);
            // 更新被点击项图标
            UiUtils.getListViewAdapter(this.listView, PlaylistDialogAdapter.class).notifyDataSetChanged();
        });

        // 获取下一个播放模式并设置界面
        this.textViewPlayMode.setOnClickListener(v -> {
            assert onClickListener != null;
            onClickListener.onPlayModeBtnClick();
            this.updateModeInterface();
        });

        // 收藏按钮的点击事件
        this.textViewCollect.setOnClickListener(v -> {
            assert onClickListener != null;
            onClickListener.onCollectBtnClick();
        });

        //  清空按钮的点击事件
        this.imageViewClear.setOnClickListener(v -> {
            OptionDialog of = OptionDialog.of(context, getString(R.string.sure_to_clear_playlist));
            of.setPositiveButton(getString(R.string.clear), () -> {
                assert onClickListener != null;
                onClickListener.onClearBtnClick();
                UiUtils.getListViewAdapter(this.listView, PlaylistDialogAdapter.class).notifyDataSetChanged();
                dismiss();
            });
            of.setNegativeButton(null);
            of.show();
        });
    }

    /**
     * 设置播放模式按钮的文字和图片
     */
    private void updateModeInterface() {
        int mode = this.playlist.getMode();
        PlayMode playMode = Playlist.getModeByValue(mode, this.context);
        Drawable drawable = this.context.getResources().getDrawable(playMode.getImageId(), null);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        this.textViewPlayMode.setCompoundDrawables(drawable, null, null, null);
        this.textViewPlayMode.setText(playMode.getLabel() + "  (" + this.playlist.size() + ")");
    }

    public void updateAdapterInterface() {
        // 更新当前播放项的图标
        PlaylistDialogAdapter adapter = UiUtils.getListViewAdapter(this.listView, PlaylistDialogAdapter.class);
        adapter.notifyDataSetChanged();
    }

    public interface OnClickListener {
        /**
         * 当播放列表的item 被点击时回调
         *
         * @param position position
         */
        void onItemClick(int position);

        /**
         * 当播放模式按钮被点击时回调
         */
        void onPlayModeBtnClick();

        void onCollectBtnClick();

        void onClearBtnClick();

        /**
         * 当item 的定位按钮被点击时回调, 定位到某歌单或页面
         *
         * @param position pos
         */
        void onLocateBtnClick(int position);

        /**
         * 当item 的删除按钮被点击时回调, 删除播放列表中的某一个
         *
         * @param position pos
         * @return playlist's index.
         */
        int onDeleteBtnClick(int position);
    }
}
