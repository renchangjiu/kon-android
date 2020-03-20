package com.htt.kon.adapter.list.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.activity.BaseActivity;
import com.htt.kon.activity.LocalMusicActivity;
import com.htt.kon.activity.MainActivity;
import com.htt.kon.bean.MusicList;
import com.htt.kon.constant.CommonConstant;
import com.htt.kon.service.database.MusicListDbService;

import java.util.ArrayList;
import java.util.List;


/**
 * @author su
 * @date 2020/03/07 21:11
 */
public class MusicListDialogAdapter extends BaseAdapter {

    private MusicListDbService service;

    private BaseActivity activity;

    private List<MusicList> res = new ArrayList<>();

    public MusicListDialogAdapter(Context activity) {
        this.activity = (BaseActivity) activity;
        this.service = MusicListDbService.of(this.activity);
        this.initRes();
    }

    public void initRes() {
        this.service.list(musicLists -> {
            this.activity.runOnUiThread(() -> {
                this.res.clear();
                for (MusicList musicList : musicLists) {
                    if (!musicList.getId().equals(CommonConstant.MID_LOCAL_MUSIC)) {
                        this.res.add(musicList);
                    }
                }
                this.notifyDataSetChanged();
            });
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ml_dialog, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.limld_imageView);
            holder.textViewName = view.findViewById(R.id.limld_textViewName);
            holder.textViewCount = view.findViewById(R.id.limld_textViewCount);
            view.setTag(holder);
        }

        MusicList item = this.getItem(position);
        // holder.imageView.setImageResource(item.imageId);
        holder.textViewName.setText(item.getName());
        String format = this.activity.getString(R.string.music_list_music_count);
        holder.textViewCount.setText(String.format(format, item.getMusics().size()));
        return view;
    }


    @Override
    public int getCount() {
        return this.res.size();
    }

    @Override
    public MusicList getItem(int position) {
        return this.res.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.getItem(position).getId();
    }

    private static class ViewHolder {
        private ImageView imageView;
        private TextView textViewName;
        private TextView textViewCount;
    }

}
