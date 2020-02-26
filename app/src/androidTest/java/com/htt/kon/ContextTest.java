package com.htt.kon;

import android.app.Notification;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.htt.kon.bean.Mp3Metadata;
import com.htt.kon.bean.Music;
import com.htt.kon.notification.PlayNotification;
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
        Playlist playlist = App.getApp().getPlaylist();
        Notification notification = PlayNotification.ofTwo(context, playlist.getCurMusic());
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(1, notification);
        LogUtils.e();
    }


}
