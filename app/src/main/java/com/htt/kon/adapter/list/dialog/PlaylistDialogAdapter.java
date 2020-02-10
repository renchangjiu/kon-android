package com.htt.kon.adapter.list.dialog;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.service.Playlist;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/08 20:31
 */
public class PlaylistDialogAdapter extends BaseAdapter {

    private static final int SIGNAL_LOCATE_BTN_CLICK = 1;
    private static final int SIGNAL_DELETE_BTN_CLICK = 2;

    private Playlist playlist;

    private final App app;

    // private int clickPos = -1;

    private final int lightBlackColor;

    @Setter
    private OnClickListener onClickListener;

    public PlaylistDialogAdapter() {
        this.app = App.getApp();
        this.playlist = this.app.getPlaylist();
        // this.clickPos = clickPos;
        lightBlackColor = ContextCompat.getColor(this.app, R.color.lightBlack);
    }

    /**
     * 设置被点击项的pos, 即是当前播放的pos
     */
    // public void setClickPos(int clickPos) {
    //     if (this.clickPos == clickPos) {
    //         return;
    //     }
    //     this.clickPos = clickPos;
    //     this.notifyDataSetChanged();
    // }

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

        this.setEvent(holder, position);

        Music item = this.getItem(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewArtist.setText(this.app.getString(R.string.playlist_title_artist_sep) + item.getArtist());
        if (this.playlist.getIndex() == position) {
            holder.imageViewPlay.setVisibility(View.VISIBLE);
            holder.imageViewLocate.setVisibility(View.VISIBLE);
            holder.textViewTitle.setTextColor(Color.RED);
            holder.textViewArtist.setTextColor(Color.RED);
        } else {
            holder.imageViewPlay.setVisibility(View.GONE);
            holder.imageViewLocate.setVisibility(View.GONE);
            holder.textViewTitle.setTextColor(this.lightBlackColor);
            holder.textViewArtist.setTextColor(Color.GRAY);
        }
        return view;
    }

    private void setEvent(ViewHolder holder, int position) {
        holder.imageViewLocate.setOnClickListener(v -> {
            this.emit(SIGNAL_LOCATE_BTN_CLICK, position);
        });
        holder.imageViewDelete.setOnClickListener(v -> {
            this.emit(SIGNAL_DELETE_BTN_CLICK, position);
        });
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

    /**
     * 回调事件监听器的方法
     *
     * @param signal flag
     * @param data   可选的参数
     * @return 可选的返回值
     */
    private Object emit(int signal, Object data) {
        if (this.onClickListener == null) {
            return null;
        }
        switch (signal) {
            case SIGNAL_LOCATE_BTN_CLICK:
                this.onClickListener.onLocateBtnClick((Integer) data);
                return null;
            case SIGNAL_DELETE_BTN_CLICK:
                this.onClickListener.onDeleteBtnClick((Integer) data);
                return null;
            default:
                return null;
        }
    }

    public interface OnClickListener {
        void onLocateBtnClick(int position);

        void onDeleteBtnClick(int position);
    }

    private class ViewHolder {
        private ImageView imageViewPlay;
        private TextView textViewTitle;
        private TextView textViewArtist;
        private ImageView imageViewLocate;
        private ImageView imageViewDelete;
    }
}
