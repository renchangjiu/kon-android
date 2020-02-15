package com.htt.kon.adapter.list;

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
import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2020/02/15 15:06
 */
public class LocalMusicArtistAdapter extends BaseAdapter {

    private List<ItemData> res;

    private LocalMusicActivity activity;

    private Playlist playlist;

    @Setter
    private OnOptionClickListener onOptionClickListener;

    public LocalMusicArtistAdapter(List<ItemData> res, Context context) {
        this.res = res;
        this.activity = (LocalMusicActivity) context;
        this.playlist = App.getApp().getPlaylist();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_local_music_artist, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.lilma_imageView);
            holder.textViewArtist = view.findViewById(R.id.lilma_textViewArtist);
            holder.textViewCount = view.findViewById(R.id.lilma_textViewCount);
            holder.imageViewOption = view.findViewById(R.id.lilma_imageViewOption);
            view.setTag(holder);
        }

        ItemData item = this.getItem(position);

        // 单击右侧图标
        List<Music> musics = item.getMusics();
        holder.imageViewOption.setOnClickListener(v -> {
            CommonDialogFragment dialog = CommonDialogFragment.ofArtist(item.getArtist(), JsonUtils.bean2Json(musics));
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
        holder.textViewArtist.setText(item.getArtist());

        String format = this.activity.getString(R.string.lilma_music_count);
        holder.textViewCount.setText(String.format(format, musics.size()));
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

    private class ViewHolder {
        /**
         * TODO: 歌手头像考虑爬取网易云音乐数据
         */
        private ImageView imageView;
        private TextView textViewArtist;
        private TextView textViewCount;
        private ImageView imageViewOption;
    }

    @Getter
    @Setter
    @ToString
    public static class ItemData {
        private String artist;
        private List<Music> musics;

    }

    public interface OnOptionClickListener {
        void onClick(CommonDialogItem item);
    }
}
