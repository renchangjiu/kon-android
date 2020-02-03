package com.htt.kon;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileMetadataParser;
import com.htt.kon.util.MusicFileSearcher;
import com.htt.kon.util.StorageUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ContextTest {
    @Test
    public void useAppContext() throws IOException, InterruptedException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // String path = "/storage/emulated/0/listen.mp3";
        // Mp3Metadata parse = MusicFileMetadataParser.parse(path);

        long l = System.currentTimeMillis();
        long l1 = System.currentTimeMillis() - l;

        String sdcardRootPath = StorageUtils.getSdcardRootPathR(context);
        List<String> list = MusicFileSearcher.search(sdcardRootPath);

        String externalRootPath = StorageUtils.getExternalRootPath(context);
        List<String> list1 = MusicFileSearcher.search(externalRootPath);

        list.addAll(list1);

        LogUtils.e();
    }


}
