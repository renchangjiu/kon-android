package com.htt.kon.adapter.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.htt.kon.R;
import com.htt.kon.activity.MainActivity;
import com.htt.kon.constant.MidConstant;
import com.htt.kon.service.database.MusicDbService;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;


/**
 * @author su
 * @date 2020/02/02 19:18
 */
public class LocalManagerAdapter extends BaseAdapter {

    private MusicDbService musicDbService;

    private MainActivity activity;

    private List<ItemData> res;

    public LocalManagerAdapter(Context activity) {
        this.activity = (MainActivity) activity;
        this.musicDbService = MusicDbService.of(this.activity);
        this.initItemData();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_local_manager, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.lilm_imageView);
            holder.textViewTitle = view.findViewById(R.id.lilm_textViewTitle);
            holder.textViewCount = view.findViewById(R.id.lilm_textViewCount);
            view.setTag(holder);
        }

        ItemData itemData = this.getItem(position);
        holder.imageView.setImageResource(itemData.imageId);
        holder.textViewTitle.setText(itemData.itemTitle);
        String format = this.activity.getString(R.string.item_count);
        holder.textViewCount.setText(String.format(format, itemData.count));
        return view;
    }

    private void initItemData() {
        ItemData item1 = new ItemData();
        item1.imageId = R.drawable.music_icon_local;
        item1.itemTitle = this.activity.getString(R.string.local_music);
        new Thread(() -> {
            item1.count = this.musicDbService.list(MidConstant.MID_LOCAL_MUSIC).size();
            this.activity.runOnUiThread(this::notifyDataSetChanged);
        }).start();


        ItemData item2 = new ItemData();
        item2.imageId = R.drawable.music_icon_recently_played;
        item2.itemTitle = this.activity.getString(R.string.recently_played);
        item2.count = 0;

        ItemData item3 = new ItemData();
        item3.imageId = R.drawable.music_icon_download;
        item3.itemTitle = this.activity.getString(R.string.download_manage);
        item3.count = 0;

        ItemData item4 = new ItemData();
        item4.imageId = R.drawable.music_icon_my_radio_station;
        item4.itemTitle = this.activity.getString(R.string.my_radio_station);
        item4.count = 0;

        ItemData item5 = new ItemData();
        item5.imageId = R.drawable.music_icon_my_collection;
        item5.itemTitle = this.activity.getString(R.string.my_collection);
        item5.count = 0;

        this.res = new ArrayList<>();
        this.res.add(item1);
        this.res.add(item2);
        this.res.add(item3);
        this.res.add(item4);
        this.res.add(item5);
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
        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewCount;
    }

    private static class ItemData {
        private int imageId;
        private String itemTitle;
        private int count;
    }
}
