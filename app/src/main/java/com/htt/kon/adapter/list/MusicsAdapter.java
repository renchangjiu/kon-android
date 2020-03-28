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
import com.htt.kon.adapter.AsyncAdapter;
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
public class MusicsAdapter extends BaseAdapter implements AsyncAdapter {

    private List<Music> res;

    private BaseActivity activity;

    private Playlist playlist;

    private MusicDbService musicDbService;

    private long mlId;

    @Setter
    private OnOptionClickListener onOptionClickListener;

    /**
     * @param mlId 歌单id
     */
    public MusicsAdapter(Context context, long mlId) {
        this.activity = (BaseActivity) context;
        this.playlist = App.getPlaylist();
        this.musicDbService = MusicDbService.of(this.activity);
        this.res = new ArrayList<>();
        this.mlId = mlId;
        this.updateRes();
    }

    @Override
    public void updateRes() {
        this.musicDbService.list(mlId, musics -> {
            this.activity.runOnUiThread(() -> {
                this.res.clear();
                this.res.addAll(musics);
                super.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void clearRes() {
        this.res.clear();
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
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_PLAY_NEXT).setName(context.getString(R.string.cdf_play_next)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_COLLECT).setName(context.getString(R.string.cdf_collect)).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_ARTIST).setName(String.format(context.getString(R.string.cdf_artist), item.getArtist())).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_ALBUM).setName(String.format(context.getString(R.string.cdf_album), item.getAlbum())).setData(data));
            items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_DELETE).setName(context.getString(R.string.cdf_delete)).setData(data));

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
