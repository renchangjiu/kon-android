package com.htt.kon.adapter.list.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author su
 * @date 2020/02/23 19:52
 */
public class DirAdapter extends BaseAdapter implements LocalMusicFragmentAdapter {

    private List<ItemData> res;

    private LocalMusicActivity activity;

    private Playlist playlist;

    @Setter
    private OnOptionClickListener onOptionClickListener;

    public DirAdapter(List<ItemData> res, Context context) {
        this.res = res;
        this.activity = (LocalMusicActivity) context;
        this.playlist = App.getPlaylist();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        Context context = parent.getContext();
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_local_music_dir, parent, false);
            holder = new ViewHolder();
            holder.textViewDirName = view.findViewById(R.id.lilmd_textViewDirName);
            holder.textViewCount = view.findViewById(R.id.lilmd_textViewCount);
            holder.imageViewOption = view.findViewById(R.id.lilmd_imageViewOption);
            view.setTag(holder);
        }

        ItemData item = this.getItem(position);
        File file = new File(item.getPath());
        List<Music> musics = item.getMusics();
        // 右侧图标点击事件
        holder.imageViewOption.setOnClickListener(v -> {
            String format = context.getString(R.string.cdf_dialog_title_dir);

            List<CommonDialogItem> items = new ArrayList<>();
            String data = JsonUtils.bean2Json(item);
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_PLAY_NEXT).setName(context.getString(R.string.cdf_play_next)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_COLLECT).setName(context.getString(R.string.cdf_collect)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_DELETE).setName(context.getString(R.string.cdf_delete)).setData(data));
            CommonDialog dialog = CommonDialog.of(String.format(format, item.getPath()), items);

            dialog.show(this.activity.getSupportFragmentManager(), "1");
            dialog.setOnClickListener((CommonDialogItem item1) -> {
                // 回调方法
                Optional.of(this.onOptionClickListener).ifPresent(v1 -> v1.onClick(item1));
            });
        });

        if (this.playlist.isNotEmpty() && musics.contains(this.playlist.getCurMusic())) {
            holder.imageViewOption.setImageResource(R.drawable.list_item_play);
        } else {
            holder.imageViewOption.setImageResource(R.drawable.list_item_option);
        }

        holder.textViewDirName.setText(file.getName());
        String format = this.activity.getString(R.string.lilmal_music_count);
        holder.textViewCount.setText(String.format(format, musics.size(), file.getParent()));
        return view;
    }

    @Override
    public int getCount() {
        return this.res.size();
    }

    @Override
    public ItemData getItem(int position) {
        return this.res.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        private TextView textViewDirName;
        private TextView textViewCount;
        private ImageView imageViewOption;
    }

    @Getter
    @Setter
    @ToString
    public static class ItemData {
        private String path;
        private List<Music> musics;

    }

    public interface OnOptionClickListener {
        void onClick(CommonDialogItem item);
    }
}
