package com.htt.kon.util;

import android.media.MediaMetadataRetriever;

import com.htt.kon.bean.Mp3Metadata;

/**
 * 解析音乐文件的 tag 信息
 *
 * @author su
 * @date 2020/02/02 21:07
 */
public class MusicFileMetadataParser {

    public static Mp3Metadata parse(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        byte[] embeddedPicture = mmr.getEmbeddedPicture();
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String bitRate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);

        Mp3Metadata metadata = new Mp3Metadata();
        metadata.setImage(embeddedPicture);
        metadata.setTitle(title);
        metadata.setArtist(artist);
        metadata.setAlbum(album);
        metadata.setDuration(Integer.valueOf(duration));
        metadata.setBitRate(Integer.valueOf(bitRate) / 1000);
        return metadata;
    }

}
