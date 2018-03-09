package kr.clude.hfcode.sender.util;

import android.util.Log;

import static kr.clude.hfcode.base.Config.MAX_MESSAGE_INDEX;
import static kr.clude.hfcode.base.Config.MAX_FREQUENCY;
import static kr.clude.hfcode.base.Config.MIN_FREQUENCY;
import static kr.clude.hfcode.base.Config.SAMPLE_RATE;

/**
 * Created by joonyoung.yi on 2017. 3. 25..
 */

public class AudioTrackGenerator {

    private static final String TAG = "AudioTrackGenerator";

    public static short[] generate(int data, int seconds) {

        int duration = seconds * SAMPLE_RATE;
        short[] mBuffer = new short[duration];

        double frequency = MIN_FREQUENCY + (MAX_FREQUENCY - MIN_FREQUENCY) * ((double) data - 1) / (double) (MAX_MESSAGE_INDEX);
        Log.d(TAG, "frequency -> " + frequency);
        if (data > 0 && data <= MAX_MESSAGE_INDEX) {
            for (int i = 0; i < duration; i++) {
                double A = -4 * ((double) i) * ((double) i - (double) duration) / (double) duration / (double) duration;
                double point = A * (Math.sin((2.0 * Math.PI * i * frequency / SAMPLE_RATE)) * Short.MAX_VALUE);
                mBuffer[i] = (short) point;
            }
        }

        return mBuffer;
    }
}
