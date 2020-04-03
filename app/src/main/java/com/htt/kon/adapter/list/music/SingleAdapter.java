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
import com.htt.kon.activity.BaseActivity;
import com.htt.kon.adapter.list.CommonAdapter;
import com.htt.kon.adapter.list.OnOptionClickListener;
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author su
 * @date 2020/02/04 08:33
 */
public class SingleAdapter extends BaseAdapter implements CommonAdapter {

    private List<Music> res;

    private BaseActivity activity;

    private Playlist playlist;

    private OnOptionClickListener onOptionClickListener;

    public SingleAdapter(Context context) {
        this.activity = (BaseActivity) context;
        this.playlist = App.getPlaylist();
        this.res = new ArrayList<>();
    }

    public SingleAdapter(List<Music> res, Context context) {
        this.activity = (BaseActivity) context;
        this.playlist = App.getPlaylist();
        this.res = res;
    }

    @Override
    public void updateRes(List<Music> musics) {
        this.res.clear();
        this.res.addAll(musics);
        super.notifyDataSetChanged();
    }

    public List<Music> getRes() {
        return this.res;
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

            ItemData var = new ItemData();
            var.setMusics(new ArrayList<>(Collections.singletonList(item)));
            String data = JsonUtils.bean2Json(var);

            items.add(CommonDialog.getItem(CommonDialog.TAG_PLAY_NEXT, context.getString(R.string.cdf_play_next), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_COLLECT, context.getString(R.string.cdf_collect), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_ARTIST, String.format(context.getString(R.string.cdf_artist), item.getArtist()), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_ALBUM, String.format(context.getString(R.string.cdf_album), item.getAlbum()), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_DELETE, context.getString(R.string.cdf_delete), data));

            CommonDialog dialog = CommonDialog.of(String.format(format, item.getTitle()), items);
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

    @Override
    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.onOptionClickListener = listener;
    }


    private static class ViewHolder {
        private ImageView imageViewPlay;
        private TextView textViewTitle;
        private TextView textViewArtistAlbum;
        private ImageView imageViewOption;
    }

}
