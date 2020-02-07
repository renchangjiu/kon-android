package com.htt.kon.adapter.pager;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.htt.kon.R;
import com.htt.kon.bean.Music;
import com.htt.kon.bean.Playlist;
import com.htt.kon.util.LogUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author su
 * @date 2020/02/07 21:09
 */
public class PlayBarAdapter extends PagerAdapter {
    private Playlist playlist;
    private Context context;

    // /**
    //  * 封面图片
    //  */
    // private ImageView imageViewCover;
    //
    // /**
    //  * 歌名栏
    //  */
    // private TextView textViewTitle;
    //
    // /**
    //  * 歌手及歌词栏
    //  */
    // private TextView textViewArtist;

    public PlayBarAdapter(Playlist playlist, Context context) {
        this.playlist = playlist;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.pager_play_bar, container, false);
        container.addView(view);
        this.init(view, position);
        return view;
    }

    private void init(View view, int position) {
        ImageView  imageViewCover = view.findViewById(R.id.lipb_imageViewCover);
        TextView  textViewTitle = view.findViewById(R.id.lipb_textViewTitle);
        TextView  textViewArtist = view.findViewById(R.id.lipb_textViewArtist);

        Music music = this.playlist.getMusic(position);
        if (StringUtils.isNotEmpty(music.getImage())) {
            imageViewCover.setImageBitmap(BitmapFactory.decodeFile(music.getImage()));
        }
        textViewTitle.setText(music.getTitle());
        textViewArtist.setText(music.getArtist());

        view.setOnClickListener(v -> {
            LogUtils.e();
        });
    }

    @Override
    public int getCount() {
        return this.playlist.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
