package com.htt.kon.adapter.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.CommonDialogFragment;
import com.htt.kon.util.LogUtils;

import java.util.List;

/**
 * @author su
 * @date 2020/02/04 08:33
 */
public class SingleAdapter extends BaseAdapter {

    private List<Music> res;
    private ListView listView;
    private LocalMusicActivity activity;
    private int playIconShowPos = -1;


    public SingleAdapter(List<Music> res, ListView listView, Context activity) {
        this.res = res;
        this.listView = listView;
        this.activity = (LocalMusicActivity) activity;
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

            this.listView.setOnItemClickListener((parent1, view1, position1, id) -> {
                this.playIconShowPos = position1 - 1;
                this.notifyDataSetChanged();
            });

            // 单击右侧图标
            holder.imageViewOption.setOnClickListener(v -> {
                LogUtils.e("option click: " + position);
                CommonDialogFragment dialog = CommonDialogFragment.ofSingle(1);
                dialog.show(this.activity.getSupportFragmentManager(), "1");
                dialog.setOnSelectListener(tag -> {
                    LogUtils.e(tag);
                });
            });

        }


        if (this.playIconShowPos == position) {
            holder.imageViewPlay.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewPlay.setVisibility(View.GONE);
        }
        Music item = this.getItem(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewArtistAlbum.setText(item.getArtist() + " " + item.getAlbum());
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

}
