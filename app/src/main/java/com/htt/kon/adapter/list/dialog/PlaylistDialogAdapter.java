package com.htt.kon.adapter.list.dialog;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.Playlist;
import com.htt.kon.util.LogUtils;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/08 20:31
 */
public class PlaylistDialogAdapter extends BaseAdapter {

    private Playlist playlist;

    private final App app;

    private int clickPos = -1;
    private final int lightBlackColor;


    public PlaylistDialogAdapter(int clickPos) {
        this.app = App.getApp();
        this.playlist = this.app.getPlaylist();
        this.clickPos = clickPos;
        lightBlackColor = ContextCompat.getColor(this.app, R.color.lightBlack);
    }

    public void setClickPos(int clickPos) {
        this.clickPos = clickPos;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_playlist, parent, false);
            holder = new ViewHolder();
            holder.imageViewPlay = view.findViewById(R.id.lip_imageViewPlay);
            holder.textViewTitle = view.findViewById(R.id.lip_textViewTitle);
            holder.textViewArtist = view.findViewById(R.id.lip_textViewArtist);
            holder.imageViewLocate = view.findViewById(R.id.lip_imageViewLocate);
            holder.imageViewDelete = view.findViewById(R.id.lip_imageViewDelete);
            view.setTag(holder);
        }

        Music item = this.getItem(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewArtist.setText(this.app.getString(R.string.playlist_title_artist_sep) + item.getArtist());
        if (this.clickPos == position) {
            holder.imageViewPlay.setVisibility(View.VISIBLE);
            holder.imageViewLocate.setVisibility(View.VISIBLE);
            holder.textViewTitle.setTextColor(Color.RED);
            holder.textViewArtist.setTextColor(Color.RED);
        } else {
            holder.imageViewPlay.setVisibility(View.GONE);
            holder.imageViewLocate.setVisibility(View.GONE);
            holder.textViewTitle.setTextColor(this.lightBlackColor);
            holder.textViewArtist.setTextColor(this.lightBlackColor);
        }
        return view;
    }

    @Override
    public int getCount() {
        return this.playlist.size();
    }

    @Override
    public Music getItem(int position) {
        return this.playlist.getMusic(position);
    }

    @Override
    public long getItemId(int position) {
        return this.getItem(position).getId();
    }

    private class ViewHolder {
        private ImageView imageViewPlay;
        private TextView textViewTitle;
        private TextView textViewArtist;
        private ImageView imageViewLocate;
        private ImageView imageViewDelete;
    }
}
