// package com.htt.kon.adapter.list;
//
// import android.content.Context;
// import android.view.LayoutInflater;
// import android.view.View;
// import android.view.ViewGroup;
// import android.widget.BaseAdapter;
// import android.widget.ImageView;
// import android.widget.TextView;
//
// import com.htt.kon.R;
// import com.htt.kon.activity.MainActivity;
// import com.htt.kon.adapter.list.music.SingleAdapter;
// import com.htt.kon.bean.CommonDialogItem;
// import com.htt.kon.bean.Music;
// import com.htt.kon.bean.MusicList;
// import com.htt.kon.dialog.CommonDialog;
// import com.htt.kon.service.database.MusicListDbService;
// import com.htt.kon.util.JsonUtils;
// import com.htt.kon.util.stream.Optional;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * @author su
//  * @date 2020/03/15 21:23
//  */
// public class MusicsAdapter extends BaseAdapter {
//
//
//     private List<Music> res;
//
//     public MusicsAdapter(List<Music> res, Context activity) {
//     }
//
//     @Override
//     public View getView(int position, View convertView, ViewGroup parent) {
//         View view;
//         SingleAdapter.ViewHolder holder;
//         Context context = parent.getContext();
//         if (convertView != null) {
//             view = convertView;
//             holder = (SingleAdapter.ViewHolder) view.getTag();
//         } else {
//             view = LayoutInflater.from(context).inflate(R.layout.list_item_local_music_single, parent, false);
//             holder = new SingleAdapter.ViewHolder();
//             holder.imageViewPlay = view.findViewById(R.id.lilma_imageView);
//             holder.imageViewOption = view.findViewById(R.id.lilms_imageViewOption);
//             holder.textViewTitle = view.findViewById(R.id.lilma_textViewArtist);
//             holder.textViewArtistAlbum = view.findViewById(R.id.lilma_textViewCount);
//             view.setTag(holder);
//         }
//
//         Music item = this.getItem(position);
//
//         // 右侧按钮点击事件
//         holder.imageViewOption.setOnClickListener(v -> {
//             String format = context.getString(R.string.cdf_dialog_title_single);
//             List<CommonDialogItem> items = new ArrayList<>();
//             String data = JsonUtils.bean2Json(item);
//             items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_PLAY_NEXT).setName(context.getString(R.string.cdf_play_next)).setData(data));
//             items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_COLLECT).setName(context.getString(R.string.cdf_collect)).setData(data));
//             items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_ARTIST).setName(String.format(context.getString(R.string.cdf_artist), item.getArtist())).setData(data));
//             items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_ALBUM).setName(String.format(context.getString(R.string.cdf_album), item.getAlbum())).setData(data));
//             items.add(CommonDialog.FULL_ITEMS.get(CommonDialog.TAG_DELETE).setName(context.getString(R.string.cdf_delete)).setData(data));
//
//             CommonDialog dialog = CommonDialog.of(String.format(format, item.getTitle()), items);
//             dialog.show(this.activity.getSupportFragmentManager(), "1");
//             dialog.setOnClickListener((CommonDialogItem vv) -> {
//                 // 回调方法
//                 Optional.of(this.onOptionClickListener).ifPresent(v1 -> v1.onClick(vv));
//             });
//         });
//
//
//         if (this.playlist.isNotEmpty() && this.playlist.getCurMusic().getId().equals(item.getId())) {
//             holder.imageViewPlay.setVisibility(View.VISIBLE);
//         } else {
//             holder.imageViewPlay.setVisibility(View.GONE);
//         }
//         holder.textViewTitle.setText(item.getTitle());
//         String format = this.activity.getString(R.string.artist_album);
//         holder.textViewArtistAlbum.setText(String.format(format, item.getArtist(), item.getAlbum()));
//         return view;
//     }
//
//
//     @Override
//     public int getCount() {
//         return this.res.size();
//     }
//
//     @Override
//     public Music getItem(int position) {
//         return this.res.get(position);
//     }
//
//     @Override
//     public long getItemId(int position) {
//         return this.getItem(position).getId();
//     }
//
//     private static class ViewHolder {
//         private ImageView imageView;
//         private TextView textViewName;
//         private TextView textViewCount;
//         private ImageView imageViewOption;
//     }
//
// }
