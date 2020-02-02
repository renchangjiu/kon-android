package com.htt.kon;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.util.LogUtils;
import com.htt.kon.util.MusicFileMetadataParser;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ContextTest {
    @Test
    public void useAppContext() throws IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String path = "/storage/emulated/0/listen.mp3";
        Mp3Metadata parse = MusicFileMetadataParser.parse(path);
        LogUtils.e();
    }
}
