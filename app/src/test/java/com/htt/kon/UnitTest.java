package com.htt.kon;

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
        String command = "cmd.exe /c adb shell input tap 500 500";
        int i = 0;
        while (true) {
            Runtime.getRuntime().exec(command);
            Thread.sleep(100);
            i++;
            System.out.println(i);
        }
    }
}