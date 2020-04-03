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
import com.htt.kon.bean.CommonDialogItem;
import com.htt.kon.bean.MusicList;
import com.htt.kon.constant.CommonConstant;
import com.htt.kon.dialog.CommonDialog;
import com.htt.kon.util.JsonUtils;
import com.htt.kon.util.stream.Optional;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;


/**
 * @author su
 * @date 2020/03/04 21:15
 */
public class MusicListAdapter extends BaseAdapter {


    private MainActivity activity;

    private List<MusicList> res;

    @Setter
    private OnOptionClickListener onOptionClickListener;

    public MusicListAdapter(Context activity) {
        this.activity = (MainActivity) activity;
        this.res = new ArrayList<>();
    }

    public void updateRes(List<MusicList> musicLists) {
        this.res.clear();
        this.res.addAll(musicLists);
        this.res.removeIf(v -> v.getId().equals(CommonConstant.MID_LOCAL_MUSIC));
        this.notifyDataSetChanged();
    }

    public void clearRes() {
        this.res.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ml, parent, false);
            holder = new ViewHolder();
            holder.imageView = view.findViewById(R.id.liml_imageView);
            holder.textViewName = view.findViewById(R.id.liml_textViewName);
            holder.textViewCount = view.findViewById(R.id.liml_textViewCount);
            holder.imageViewOption = view.findViewById(R.id.liml_imageViewOption);
            view.setTag(holder);
        }

        MusicList item = this.getItem(position);
        initOptionEvent(holder, item);
        // holder.imageView.setImageResource(item.imageId);
        holder.textViewName.setText(item.getName());
        String format = this.activity.getString(R.string.music_list_music_count);
        holder.textViewCount.setText(String.format(format, item.getMusics().size()));
        return view;
    }

    private void initOptionEvent(ViewHolder holder, MusicList item) {
        // 右侧按钮点击事件
        holder.imageViewOption.setOnClickListener(v -> {
            String format = activity.getString(R.string.cdf_dialog_title_ml);
            List<CommonDialogItem> items = new ArrayList<>();
            String data = JsonUtils.bean2Json(item);
            items.add(CommonDialog.getItem(CommonDialog.TAG_DOWNLOAD, activity.getString(R.string.download), data));
            items.add(CommonDialog.getItem(CommonDialog.TAG_SHARE, activity.getString(R.string.share), data));
            if (!item.getId().equals(CommonConstant.MID_MY_FAVORITE)) {
                items.add(CommonDialog.getItem(CommonDialog.TAG_EDIT_ML, activity.getString(R.string.cdf_edit_ml), data));
                items.add(CommonDialog.getItem(CommonDialog.TAG_DELETE, activity.getString(R.string.cdf_delete), data));
            }
            CommonDialog dialog = CommonDialog.of(String.format(format, item.getName()), items);
            dialog.show(this.activity.getSupportFragmentManager(), "1");
            dialog.setOnClickListener((CommonDialogItem vv) -> {
                // 回调方法
                Optional.of(this.onOptionClickListener).ifPresent(v1 -> v1.onClick(vv));
            });
        });
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
        private ImageView imageViewOption;
    }

}
