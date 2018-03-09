package kr.clude.hfcodesender.libs;

import android.util.Log;

import static kr.clude.hfcodesender.libs.Config.MAX_FREQUENCY;
import static kr.clude.hfcodesender.libs.Config.MAX_MESSAGE_INDEX;
import static kr.clude.hfcodesender.libs.Config.MIN_FREQUENCY;
import static kr.clude.hfcodesender.libs.Config.SAMPLE_RATE;

/**
 * Created by joonyoung.yi on 2017. 3. 25..
 */

public class AudioTrackGenerator {

    private static final String TAG = "AudioTrackGenerator";

    public static short[] generate(int data, int seconds) {

        int duration = seconds * SAMPLE_RATE;
        int WINDOW_LENGTH = SAMPLE_RATE / 8;

        short[] mBuffer = new short[duration];

        double frequency = MIN_FREQUENCY + (MAX_FREQUENCY - MIN_FREQUENCY) * ((double) data + 1) / (double) (MAX_MESSAGE_INDEX);
        Log.d(TAG, "frequency -> " + frequency);

        for (int i = 0; i < duration; i++) {
            double A = 0;
            if (i <= WINDOW_LENGTH) {
                A = (i) / WINDOW_LENGTH;
            } else if (i >= duration - WINDOW_LENGTH) {
                A = (duration - i - 1) / WINDOW_LENGTH;
            } else {
                A = 1;
            }
            double point = A * (Math.sin((2.0 * Math.PI * i * frequency / SAMPLE_RATE)) * Short.MAX_VALUE);
            mBuffer[i] = (short) point;
        }

        return mBuffer;
    }
}
