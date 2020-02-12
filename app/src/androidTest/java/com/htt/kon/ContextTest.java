package com.htt.kon;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.bean.Music;
import com.htt.kon.service.Playlist;
import com.htt.kon.util.IdWorker;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileMetadataParser;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.StorageUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ContextTest {
    @Test
    public void useAppContext() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // String path = "/storage/emulated/0/listen.mp3";
        // Mp3Metadata parse = MusicFileMetadataParser.parse(path);

        // long l = System.currentTimeMillis();
        // long l1 = System.currentTimeMillis() - l;
        //
        // String sdcardRootPath = StorageUtils.getSdcardRootPathR(context);
        // List<String> list = MusicFileSearcher.search(sdcardRootPath);
        //
        // String externalRootPath = StorageUtils.getExternalRootPath(context);
        // List<String> list1 = MusicFileSearcher.search(externalRootPath);
        //
        // list.addAll(list1);

        // preparePlaylist(context);

        getmusic(context);
        LogUtils.e();
    }

    public static void getmusic(Context context) throws Exception {
        long start = System.currentTimeMillis();
        List<String> paths = MusicFileSearcher.search(context);

        List<Music> musics = new ArrayList<>();
        String root = context.getExternalFilesDir(null).getAbsolutePath();
        File dir = new File(root + "/image/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        for (String path : paths) {
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
            music.setCreateTime(start);
            music.setDelFlag(2);
            musics.add(music);
        }
        long sep = System.currentTimeMillis() - start;
        LogUtils.e();

    }

    public void preparePlaylist(Context context) {
        try {
            String sdcardRootPath = StorageUtils.getSdcardRootPath(context);
            String externalRootPath = StorageUtils.getExternalRootPath(context);
            List<String> list = MusicFileSearcher.search(sdcardRootPath);
            list.addAll(MusicFileSearcher.search(externalRootPath));

            List<Music> musics = new ArrayList<>();
            String root = context.getExternalFilesDir(null).getAbsolutePath();
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
            Playlist playlist = Playlist.init(context);
            playlist.setMusics(musics);
            playlist.save(context);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
