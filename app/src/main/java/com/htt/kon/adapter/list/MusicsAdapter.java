package com.htt.kon.adapter.list;

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
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.Music;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

/**
 * @author su
 * @date 2020/02/04 08:33
 */
public class MusicsAdapter extends BaseAdapter implements CommonAdapter {

    private List<Music> res;

    private BaseActivity activity;

    private Playlist playlist;

    private OnOptionClickListener onOptionClickListener;

    public MusicsAdapter(Context context) {
        this.activity = (BaseActivity) context;
        this.playlist = App.getPlaylist();
        this.res = new ArrayList<>();
    }

    @Override
    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.onOptionClickListener = listener;
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
            view = LayoutInflater.from(context).inflate(R.layout.list_item_ml_musics, parent, false);
            holder = new ViewHolder();
            holder.tvSortNum = view.findViewById(R.id.limm_tvSortNum);
            holder.imageView = view.findViewById(R.id.limm_imageView);
            holder.tvTitle = view.findViewById(R.id.limm_tvTitle);
            holder.tvArtist = view.findViewById(R.id.limm_tvArtist);
            holder.ivOption = view.findViewById(R.id.limm_ivOption);
            view.setTag(holder);
        }

        Music item = this.getItem(position);

        // 右侧按钮点击事件
        holder.ivOption.setOnClickListener(v -> {
            String format = context.getString(R.string.cdf_dialog_title_single);
            List<CommonDialogItem> items = new ArrayList<>();
            String data = JsonUtils.bean2Json(item);
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
            holder.imageView.setVisibility(View.VISIBLE);
            holder.tvSortNum.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.tvSortNum.setVisibility(View.VISIBLE);
        }
        holder.tvSortNum.setText(String.format(activity.getString(R.string.digital), position + 1));
        holder.tvTitle.setText(item.getTitle());
        String format = this.activity.getString(R.string.artist_album);
        holder.tvArtist.setText(String.format(format, item.getArtist(), item.getAlbum()));
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
        private TextView tvSortNum;

        private ImageView imageView;

        private TextView tvTitle;

        private TextView tvArtist;

        private ImageView ivOption;
    }

}
