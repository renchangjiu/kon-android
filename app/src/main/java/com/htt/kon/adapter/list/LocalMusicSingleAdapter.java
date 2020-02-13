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
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.stream.Optional;

import java.util.List;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/04 08:33
 */
public class LocalMusicSingleAdapter extends BaseAdapter {

    private List<Music> res;

    private LocalMusicActivity activity;

    private Playlist playlist;

    @Setter
    private OnOptionClickListener onOptionClickListener;

    public LocalMusicSingleAdapter(List<Music> res, Context activity) {
        this.res = res;
        this.activity = (LocalMusicActivity) activity;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_local_music_single, parent, false);
            holder = new ViewHolder();
            holder.imageViewPlay = view.findViewById(R.id.lilms_imageViewPlay);
            holder.imageViewOption = view.findViewById(R.id.lilms_imageViewOption);
            holder.textViewTitle = view.findViewById(R.id.lilms_textViewTitle);
            holder.textViewArtistAlbum = view.findViewById(R.id.lilms_textViewArtistAlbum);
            view.setTag(holder);
        }

        // 单击右侧图标
        holder.imageViewOption.setOnClickListener(v -> {
            CommonDialogFragment dialog = CommonDialogFragment.ofSingle(this.getItem(position).getId());
            dialog.show(this.activity.getSupportFragmentManager(), "1");
            dialog.setOnClickListener(item -> {
                // 封装数据后回调方法
                item.setData(this.getItem(position));
                Optional.of(this.onOptionClickListener).ifPresent(v1 -> v1.onClick(item));
            });
        });

        Music item = this.getItem(position);

        if (this.playlist.isNotEmpty() && this.playlist.getCurMusic().getId().equals(item.getId())) {
            holder.imageViewPlay.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewPlay.setVisibility(View.GONE);
        }
        holder.textViewTitle.setText(item.getTitle());
        String format = this.activity.getString(R.string.artist_album);
        holder.textViewArtistAlbum.setText(String.format(format, item.getArtist(), item.getAlbum()));
        return view;
    }

    @Override
    public int getCount() {
        return this.res.size();
    }

    @Override
    public Music getItem(int position) {
        return this.res.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.getItem(position).getId();
    }

    private class ViewHolder {
        private ImageView imageViewPlay;
        private TextView textViewTitle;
        private TextView textViewArtistAlbum;
        private ImageView imageViewOption;
    }

    public interface OnOptionClickListener {
        void onClick(CommonDialogItem item);
    }
}
