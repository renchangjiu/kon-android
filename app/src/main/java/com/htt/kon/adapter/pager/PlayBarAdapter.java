package com.htt.kon.adapter.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.htt.kon.App;
import com.htt.kon.R;
import com.htt.kon.activity.MusicPlayActivity;
import com.htt.kon.bean.Music;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 底部播放栏 adapter
 *
 * @author su
 * @date 2020/02/07 21:09
 */
public class PlayBarAdapter extends PagerAdapter {

    private Playlist playlist;

    /**
     * viewPager 优化
     */
    private Queue<View> viewPool = new LinkedList<>();

    public PlayBarAdapter() {
        this.playlist = App.getApp().getPlaylist();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        View view;
        ViewHolder holder;
        if (!this.viewPool.isEmpty()) {
            view = this.viewPool.poll();
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.pager_play_bar, container, false);
            holder = new ViewHolder();
            holder.imageViewCover = view.findViewById(R.id.lipb_imageViewCover);
            holder.textViewTitle = view.findViewById(R.id.lipb_textViewTitle);
            holder.textViewArtist = view.findViewById(R.id.lipb_textViewArtist);
            view.setTag(holder);
        }
        Music music = this.playlist.getMusic(position);
        if (StringUtils.isNotEmpty(music.getImage())) {
            holder.imageViewCover.setImageBitmap(BitmapFactory.decodeFile(music.getImage()));
        } else {
            holder.imageViewCover.setImageResource(R.drawable.playbar_cover_image_default);
        }
        holder.textViewTitle.setText(music.getTitle());
        holder.textViewArtist.setText(music.getArtist());

        view.setOnClickListener(v -> {
            context.startActivity(new Intent(context, MusicPlayActivity.class));
        });

        container.addView(view);
        return view;
    }

    /**
     * 解决ViewPager 不刷新问题, 此方法非最优解, 另有更优解, 见: https://www.jianshu.com/p/266861496508
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return this.playlist.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        // 将当前 View 加入到池子中
        this.viewPool.add((View) object);
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    private class ViewHolder {
        private ImageView imageViewCover;
        private TextView textViewTitle;
        private TextView textViewArtist;
    }
}
