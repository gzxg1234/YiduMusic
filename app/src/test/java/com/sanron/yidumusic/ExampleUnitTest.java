package com.sanron.yidumusic;

import com.sanron.yidumusic.bdmusic.BMA;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        System.out.println(BMA.Song.recommendSong(10));
    }
}