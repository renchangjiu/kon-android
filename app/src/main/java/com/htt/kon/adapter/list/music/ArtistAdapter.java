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
import com.htt.kon.adapter.list.CommonAdapter;
import com.htt.kon.adapter.list.OnOptionClickListener;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author su
 * @date 2020/02/15 15:06
 */
public class ArtistAdapter extends BaseAdapter implements CommonAdapter {

    private List<ItemData> res = new ArrayList<>();

    private LocalMusicActivity activity;

    private Playlist playlist;


    private OnOptionClickListener onOptionClickListener;

    public ArtistAdapter(Context context) {
        this.activity = (LocalMusicActivity) context;
        this.playlist = App.getPlaylist();
    }

    @Override
    public void updateRes(List<Music> musics) {
        // 按歌手分类
        Map<String, List<Music>> map = this.groupByArtist(musics);
        Set<Map.Entry<String, List<Music>>> entries = map.entrySet();
        this.res.clear();
        for (Map.Entry<String, List<Music>> entry : entries) {
            ItemData item = new ItemData();
            item.setTitle(entry.getKey());
            item.setMusics(entry.getValue());
            this.res.add(item);
        }
        this.notifyDataSetChanged();
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_local_music_artist, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.lilma_imageView);
            holder.textViewArtist = view.findViewById(R.id.lilma_textViewArtist);
            holder.textViewCount = view.findViewById(R.id.lilma_textViewCount);
            holder.imageViewOption = view.findViewById(R.id.lilma_imageViewOption);
            view.setTag(holder);
        }
        ItemData item = this.getItem(position);
        List<Music> musics = item.getMusics();

        // 单击右侧图标
        holder.imageViewOption.setOnClickListener(v -> {
            String format = context.getString(R.string.cdf_dialog_title_artist);

            List<CommonDialogItem> items = new ArrayList<>();
            String data = JsonUtils.bean2Json(item);

            items.add(CommonDialog.getItem(CommonDialog.TAG_PLAY_NEXT, context.getString(R.string.cdf_play_next), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_COLLECT, context.getString(R.string.cdf_collect), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_SHARE, context.getString(R.string.cdf_share), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_DELETE, context.getString(R.string.cdf_delete), data));

            CommonDialog dialog = CommonDialog.of(String.format(format, item.getTitle()), items);
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
        holder.textViewArtist.setText(item.getTitle());

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

    @Override
    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.onOptionClickListener = listener;
    }

    /**
     * 将给定集合按 artist 分组, 若某 artist 以 / 分隔, 则视为两人
     *
     * @param musics music list
     * @return Map<artist, music musics>
     */
    private Map<String, List<Music>> groupByArtist(List<Music> musics) {
        Map<String, List<Music>> map = new HashMap<>();
        for (Music music : musics) {
            Arrays.stream(music.getArtist().split("/")).filter(StringUtils::isNotEmpty).forEach(v -> {
                List<Music> list = map.getOrDefault(v, new ArrayList<>());
                list.add(music);
                map.put(v, list);
            });
        }
        return map;
    }

    private static class ViewHolder {
        /**
         * TODO: 歌手头像考虑爬取网易云音乐数据
         */
        private ImageView imageView;
        private TextView textViewArtist;
        private TextView textViewCount;
        private ImageView imageViewOption;
    }
}
