package com.htt.kon.adapter.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.BaseActivity;
import com.htt.kon.activity.MusicsCheckedActivity;
import com.htt.kon.adapter.list.music.OnOptionClickListener;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.constant.CommonConstant;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.service.Playlist;
import com.htt.kon.service.database.MusicDbService;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/04 08:33
 */
public class MusicsCheckedAdapter extends BaseAdapter {

    private List<Music> res;

    private MusicsCheckedActivity activity;

    private final List<Integer> checkedPos = new ArrayList<>();

    public MusicsCheckedAdapter(List<Music> res, Context context) {
        this.activity = (MusicsCheckedActivity) context;
        this.res = res;
    }

    /**
     * 对选中状态取反
     *
     * @return checked count
     */
    public int checkedIf(int position) {
        if (this.checkedPos.contains(position)) {
            this.checkedPos.remove((Integer) position);
        } else {
            this.checkedPos.add(position);
        }
        super.notifyDataSetChanged();
        return this.checkedPos.size();
    }

    /**
     * 全选 or 取消全选
     *
     * @return checked count
     */
    public int checkedAll() {
        if (this.checkedPos.size() == this.res.size()) {
            this.checkedPos.clear();
        } else {
            this.checkedPos.clear();
            for (int i = 0; i < this.res.size(); i++) {
                this.checkedPos.add(i);
            }
        }
        super.notifyDataSetChanged();
        return this.checkedPos.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.list_item_musics_checked, parent, false);
            holder = new ViewHolder();
            holder.checkBox = view.findViewById(R.id.limc_checkBox);
            holder.textViewTitle = view.findViewById(R.id.limc_textViewTitle);
            holder.textViewArtistAlbum = view.findViewById(R.id.lilmc_textViewArtist);
            view.setTag(holder);
        }

        Music item = this.getItem(position);

        holder.textViewTitle.setText(item.getTitle());
        String format = this.activity.getString(R.string.artist_album);
        holder.textViewArtistAlbum.setText(String.format(format, item.getArtist(), item.getAlbum()));
        holder.checkBox.setChecked(this.checkedPos.contains(position));
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
        private CheckBox checkBox;
        private TextView textViewTitle;
        private TextView textViewArtistAlbum;
    }

}
