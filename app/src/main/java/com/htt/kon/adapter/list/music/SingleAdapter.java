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
import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/04 08:33
 */
public class SingleAdapter extends BaseAdapter implements LocalMusicFragmentAdapter {

    private List<Music> res;

    private LocalMusicActivity activity;

    private Playlist playlist;

    @Setter
    private OnOptionClickListener onOptionClickListener;

    public SingleAdapter(List<Music> res, Context activity) {
        this.res = res;
        this.activity = (LocalMusicActivity) activity;
        this.playlist = App.getApp().getPlaylist();
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
            view = LayoutInflater.from(context).inflate(R.layout.list_item_local_music_single, parent, false);
            holder = new ViewHolder();
            holder.imageViewPlay = view.findViewById(R.id.lilma_imageView);
            holder.imageViewOption = view.findViewById(R.id.lilms_imageViewOption);
            holder.textViewTitle = view.findViewById(R.id.lilma_textViewArtist);
            holder.textViewArtistAlbum = view.findViewById(R.id.lilma_textViewCount);
            view.setTag(holder);
        }

        Music item = this.getItem(position);

        // 右侧按钮点击事件
        holder.imageViewOption.setOnClickListener(v -> {
            String format = context.getString(R.string.cdf_dialog_title_single);
            List<CommonDialogItem> items = new ArrayList<>();
            items.add(new CommonDialogItem(CommonDialogFragment.TAG_PLAY_NEXT, context.getString(R.string.cdf_play_next), R.drawable.common_dialog_play_next, item));
            items.add(new CommonDialogItem(CommonDialogFragment.TAG_COLLECT, context.getString(R.string.cdf_collect), R.drawable.common_dialog_collect2music_list, item));
            items.add(new CommonDialogItem(CommonDialogFragment.TAG_ARTIST, String.format(context.getString(R.string.cdf_artist), item.getArtist()), R.drawable.common_dialog_artist, item));
            items.add(new CommonDialogItem(CommonDialogFragment.TAG_ALBUM, String.format(context.getString(R.string.cdf_album), item.getAlbum()), R.drawable.common_dialog_album, item));
            items.add(new CommonDialogItem(CommonDialogFragment.TAG_DELETE, context.getString(R.string.cdf_delete), R.drawable.common_dialog_delete, item));

            CommonDialogFragment dialog = CommonDialogFragment.of(String.format(format, item.getTitle()), items);
            dialog.show(this.activity.getSupportFragmentManager(), "1");
            dialog.setOnClickListener((CommonDialogItem vv) -> {
                // 回调方法
                Optional.of(this.onOptionClickListener).ifPresent(v1 -> v1.onClick(vv));
            });
        });


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

    private static class ViewHolder {
        private ImageView imageViewPlay;
        private TextView textViewTitle;
        private TextView textViewArtistAlbum;
        private ImageView imageViewOption;
    }

    public interface OnOptionClickListener {
        void onClick(CommonDialogItem item);
    }
}
