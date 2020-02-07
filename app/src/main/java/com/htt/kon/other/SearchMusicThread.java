package com.htt.kon.other;

import android.content.Context;

import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.bean.Music;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.MusicFileMetadataParser;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author su
 * @date 2020/02/07 20:33
 */
public class SearchMusicThread implements Runnable {
    private Context context;

    @Override
    public void run() {
        try {
            String sdcardRootPath = StorageUtils.getSdcardRootPath(this.context);
            String externalRootPath = StorageUtils.getExternalRootPath(this.context);
            List<String> list = MusicFileSearcher.search(sdcardRootPath);
            list.addAll(MusicFileSearcher.search(externalRootPath));

            List<Music> musics = new ArrayList<>();
            String root = this.context.getExternalFilesDir(null).getAbsolutePath();
            File dir = new File(root + "/image/");
            if (!dir.exists()) {
                dir.mkdir();
            }
            for (String path : list) {
                Mp3Metadata metadata = MusicFileMetadataParser.parse(path);
                Music music = new Music();
                music.setId(IdWorker.singleNextId());
                music.setMid(0L);
                music.setPath(path);
                music.setSize(new File(path).length());
                if (metadata.getImage() != null) {
                    File imageFile = new File(dir.getAbsolutePath() + "/" + music.getId() + ".png");
                    FileOutputStream out = new FileOutputStream(imageFile);
                    out.write(metadata.getImage());
                    out.flush();
                    out.close();
                    music.setImage(imageFile.getAbsolutePath());
                }
                music.setTitle(metadata.getTitle());
                music.setArtist(metadata.getArtist());
                music.setAlbum(metadata.getAlbum());
                music.setDuration(metadata.getDuration());
                music.setCreateTime(System.currentTimeMillis());
                music.setDelFlag(2);
                musics.add(music);
            }
            // TODO
            // this.playlist.setMusics(musics);
            // this.playlist.save2disk(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
