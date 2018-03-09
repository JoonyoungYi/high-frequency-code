package kr.clude.hfcode.receiver.util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by joonyoung.yi on 2017. 3. 25..
 */

public class AudioRecordFinder {

    private static final String TAG = "AudioRecordFinder";
    //private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
    private static int[] mSampleRates = new int[]{44100};


    public static AudioRecord find() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT}) {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO}) {
                    try {
                        Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }
}
