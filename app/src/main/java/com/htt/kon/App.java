package com.htt.kon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.htt.kon.service.Playlist;
import com.htt.kon.service.MusicService;
import com.htt.kon.util.LogUtils;

/**
 * @author su
 * @date 2020/02/06 19:55
 */
public class App extends Application {
    private static Context context;

    private static Playlist playlist;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e();
        context = getApplicationContext();

        playlist = Playlist.init4Disk(this);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

        // new Thread(() -> {
        //     try {
        //         String sdcardRootPath = StorageUtils.getSdcardRootPath(this);
        //         String externalRootPath = StorageUtils.getExternalRootPath(this);
        //         List<String> list = MusicFileSearcher.search(sdcardRootPath);
        //         list.addAll(MusicFileSearcher.search(externalRootPath));
        //
        //         List<Music> musics = new ArrayList<>();
        //         String root = getExternalFilesDir(null).getAbsolutePath();
        //         File dir = new File(root + "/image/");
        //         if (!dir.exists()) {
        //             dir.mkdir();
        //         }
        //         for (String path : list) {
        //             Mp3Metadata metadata = MusicFileMetadataParser.parse(path);
        //             Music music = new Music();
        //             music.setId(IdWorker.singleNextId());
        //             music.setMid(0L);
        //             music.setPath(path);
        //             music.setSize(new File(path).length());
        //             if (metadata.getImage() != null) {
        //                 File imageFile = new File(dir.getAbsolutePath() + "/" + music.getId() + ".png");
        //                 FileOutputStream out = new FileOutputStream(imageFile);
        //                 out.write(metadata.getImage());
        //                 out.flush();
        //                 out.close();
        //                 music.setImage(imageFile.getAbsolutePath());
        //             }
        //             // getResources().getr
        //             music.setTitle(metadata.getTitle());
        //             music.setArtist(metadata.getArtist());
        //             music.setAlbum(metadata.getAlbum());
        //             music.setDuration(metadata.getDuration());
        //             music.setCreateTime(System.currentTimeMillis());
        //             music.setDelFlag(2);
        //             musics.add(music);
        //         }
        //         this.playlist.setMusics(musics);
        //         this.playlist.save2disk(this);
        //
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // }).start();
    }

    public static App getApp() {
        return (App) context;
    }

    public Playlist getPlaylist() {
        return playlist;
    }
}
