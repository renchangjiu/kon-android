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
import com.htt.kon.adapter.AsyncAdapter;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.constant.CommonConstant;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.service.Playlist;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private OnOptionClickListener onOptionClickListener;

    public DirAdapter(Context context) {
        this.res = new ArrayList<>();
        this.activity = (LocalMusicActivity) context;
        this.playlist = App.getPlaylist();
    }

    @Override
    public void updateRes(List<Music> musics) {
        // 按文件夹分类
        Map<String, List<Music>> map = this.listGroupByDir(musics);
        // 封装参数
        Set<Map.Entry<String, List<Music>>> entries = map.entrySet();
        this.res.clear();
        for (Map.Entry<String, List<Music>> entry : entries) {
            ItemData item = new ItemData();
            item.setTitle(entry.getKey());
            item.setMusics(entry.getValue());
            res.add(item);
        }
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
            view = LayoutInflater.from(context).inflate(R.layout.list_item_local_music_dir, parent, false);
            holder = new ViewHolder();
            holder.textViewDirName = view.findViewById(R.id.lilmd_textViewDirName);
            holder.textViewCount = view.findViewById(R.id.lilmd_textViewCount);
            holder.imageViewOption = view.findViewById(R.id.lilmd_imageViewOption);
            view.setTag(holder);
        }

        ItemData item = this.getItem(position);
        File file = new File(item.getTitle());
        List<Music> musics = item.getMusics();
        // 右侧图标点击事件
        holder.imageViewOption.setOnClickListener(v -> {
            String format = context.getString(R.string.cdf_dialog_title_dir);

            List<CommonDialogItem> items = new ArrayList<>();
            String data = JsonUtils.bean2Json(item);

            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_PLAY_NEXT).setName(context.getString(R.string.cdf_play_next)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_COLLECT).setName(context.getString(R.string.cdf_collect)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_DELETE).setName(context.getString(R.string.cdf_delete)).setData(data));
            CommonDialog dialog = CommonDialog.of(String.format(format, new File(item.getTitle()).getName()), items);

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

    @Override
    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.onOptionClickListener = listener;
    }

    /**
     * 将给定集合按 dir 分组
     *
     * @param list music list.
     * @return Map<dir, music list>
     */
    private Map<String, List<Music>> listGroupByDir(List<Music> list) {
        Map<String, List<Music>> map = new HashMap<>();
        for (Music music : list) {
            String path = new File(music.getPath()).getParent();
            if (map.containsKey(path)) {
                map.get(path).add(music);
            } else {
                List<Music> r = new LinkedList<>();
                r.add(music);
                map.put(path, r);
            }
        }
        return map;
    }

    private static class ViewHolder {
        private TextView textViewDirName;
        private TextView textViewCount;
        private ImageView imageViewOption;
    }

}
