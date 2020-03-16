package com.htt.kon.adapter.list.music;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.adapter.AsyncAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.constant.CommonConstant;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.service.Playlist;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author su
 * @date 2020/02/16 14:34
 */
public class AlbumAdapter extends BaseAdapter implements LocalMusicFragmentAdapter, AsyncAdapter {

    private List<ItemData> res;

    private LocalMusicActivity activity;

    private Playlist playlist;

    private MusicDbService musicDbService;

    @Setter
    private OnOptionClickListener onOptionClickListener;

    public AlbumAdapter(List<ItemData> res, Context context) {
        this.res = res;
        this.activity = (LocalMusicActivity) context;
        this.playlist = App.getPlaylist();
        this.musicDbService = MusicDbService.of(context);
    }

    public AlbumAdapter(Context context) {
        this.res = new ArrayList<>();
        this.activity = (LocalMusicActivity) context;
        this.playlist = App.getPlaylist();
        this.musicDbService = MusicDbService.of(context);
        this.updateRes();
    }

    @Override
    public void updateRes() {
        this.musicDbService.list(CommonConstant.MID_LOCAL_MUSIC, musics -> {
            // 按专辑分类
            Map<String, List<Music>> map = this.musicDbService.listGroupByAlbum(musics);
            Set<Map.Entry<String, List<Music>>> entries = map.entrySet();
            this.activity.runOnUiThread(() -> {
                this.res.clear();
                for (Map.Entry<String, List<Music>> entry : entries) {
                    ItemData item = new ItemData();
                    item.setArtist(entry.getValue().get(0).getArtist());
                    item.setAlbum(entry.getKey());
                    item.setMusics(entry.getValue());
                    this.res.add(item);
                }
                super.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void clearRes() {
        this.res.clear();
        super.notifyDataSetChanged();
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
            view = LayoutInflater.from(context).inflate(R.layout.list_item_local_music_album, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.lilma_imageView);
            holder.textViewAlbum = view.findViewById(R.id.lilma_textViewAlbum);
            holder.textViewCountArtist = view.findViewById(R.id.lilma_textViewCountArtist);
            holder.imageViewOption = view.findViewById(R.id.lilma_imageViewOption);
            view.setTag(holder);
        }

        ItemData item = this.getItem(position);

        List<Music> musics = item.getMusics();
        // 右侧图标点击事件
        holder.imageViewOption.setOnClickListener(v -> {
            String format = context.getString(R.string.cdf_dialog_title_album);

            List<CommonDialogItem> items = new ArrayList<>();
            String data = JsonUtils.bean2Json(item);
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_PLAY_NEXT).setName(context.getString(R.string.cdf_play_next)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_COLLECT).setName(context.getString(R.string.cdf_collect)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_DELETE).setName(context.getString(R.string.cdf_delete)).setData(data));
            CommonDialog dialog = CommonDialog.of(String.format(format, item.getAlbum()), items);

            dialog.show(this.activity.getSupportFragmentManager(), "1");
            dialog.setOnClickListener((CommonDialogItem item1) -> {
                // 回调方法
                Optional.of(this.onOptionClickListener).ifPresent(v1 -> v1.onClick(item1));
            });
        });

        // 专辑图片使用专辑内第一首歌曲的封面
        String albumImage = musics.get(0).getImage();
        if (StringUtils.isNotEmpty(albumImage)) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(albumImage));
        } else {
            holder.imageView.setImageResource(R.drawable.list_item_album_def);
        }

        if (this.playlist.isNotEmpty() && musics.contains(this.playlist.getCurMusic())) {
            holder.imageViewOption.setImageResource(R.drawable.list_item_play);
        } else {
            holder.imageViewOption.setImageResource(R.drawable.list_item_option);
        }
        holder.textViewAlbum.setText(item.getAlbum());
        String format = this.activity.getString(R.string.lilmal_music_count);
        holder.textViewCountArtist.setText(String.format(format, musics.size(), item.getArtist()));
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
        /**
         * 专辑图片
         */
        private ImageView imageView;
        private TextView textViewAlbum;
        private TextView textViewCountArtist;
        private ImageView imageViewOption;
    }

    @Getter
    @Setter
    @ToString
    public static class ItemData {
        private String album;
        private String artist;
        private List<Music> musics;

    }

}
