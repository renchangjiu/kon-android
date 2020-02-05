package com.htt.kon.adapter.list.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htt.kon.R;
import com.htt.kon.bean.CommonDialogItem;

import java.util.List;

/**
 * @author su
 * @date 2020/02/05 20:13
 */
public class CommonDialogAdapter extends BaseAdapter {

    private List<CommonDialogItem> res;

    public CommonDialogAdapter(List<CommonDialogItem> res) {
        this.res = res;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialog_common, parent, false);
            holder = new ViewHolder();
            holder.imageViewIcon = view.findViewById(R.id.lidc_imageViewIcon);
            holder.textView = view.findViewById(R.id.lidc_textView);
            view.setTag(holder);
        }

        CommonDialogItem item = this.getItem(position);
        holder.imageViewIcon.setImageResource(item.getImageId());
        holder.textView.setText(item.getName());
        return view;
    }

    @Override
    public int getCount() {
        return this.res.size();
    }

    @Override
    public CommonDialogItem getItem(int position) {
        return this.res.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.getItem(position).getId();
    }

    private class ViewHolder {
        private ImageView imageViewIcon;
        private TextView textView;
    }
}
