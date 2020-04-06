package com.htt.kon;

import com.htt.kon.bean.Music;
import com.htt.kon.util.DataContainer;
import com.htt.kon.util.LogUtils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {
    @Test
    public void main() throws IOException, InterruptedException {
        Music music = new Music();
        music.setId(11L);
        DataContainer.set(music);
        Music music1 = DataContainer.get(Music.class);
        Music music2 = DataContainer.get(Music.class);
        // LogUtils.e();

    }
}