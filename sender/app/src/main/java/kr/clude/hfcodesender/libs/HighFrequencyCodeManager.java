package kr.clude.hfcodesender.libs;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.health.PackageHealthStats;
import android.util.Log;
import android.view.View;

import static kr.clude.hfcodesender.libs.Config.SAMPLE_RATE;

/**
 * Created by joonyoung.yi on 2017. 5. 31..
 */

public class HighFrequencyCodeManager {

    private static final String TAG = "HighFreqCodeManager";

    private RecordAsyncTask recordAsyncTask;

    private int data = -1;
    private boolean keepGoing = false;

    private AudioTrack mAudioTrack;

    private LoadingViewManager loadingViewManager;

    public HighFrequencyCodeManager(Activity activity, View progressBar) {
        loadingViewManager = new LoadingViewManager(activity, progressBar);
    }

    public void start() {
        keepGoing = true;
    }

    public void stop() {
        keepGoing = false;
    }

    public void onResume() {
        if (keepGoing) {
            loadingViewManager.showProgress(true);
            startHFCode();
        }

    }

    public void onPause() {
        if (recordAsyncTask != null) {
            recordAsyncTask.cancel(true);
        }
    }

    public void onDestroy() {

    }

    public void setData(int data) {

        loadingViewManager.showProgress(true);

        if (recordAsyncTask != null) {
            Log.d(TAG, "onCancelled");
            mAudioTrack.pause();
            mAudioTrack.stop();
            mAudioTrack = null;
            recordAsyncTask.cancel(true);
        }

        this.data = data;

        Log.d(TAG, "STARTED~~~");
        startHFCode();
    }

    public class RecordAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "STARTED");

            // AudioTrack definition
            int mBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT);
            Log.d(TAG, "bufferSize -> " + mBufferSize);

            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    mBufferSize,
                    AudioTrack.MODE_STREAM);

            mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());

            mAudioTrack.play();
            while (true) {
                Log.d(TAG, "data -> " + data);
                publishProgress();

                short[] mBuffer = AudioTrackGenerator.generate(data, 60);
                mAudioTrack.write(mBuffer, 0, mBuffer.length);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... data) {
            Log.d(TAG, "publishProgress");

            loadingViewManager.showProgress(false);
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "onCancelled");

            if (mAudioTrack != null) {
                mAudioTrack.stop();
                mAudioTrack.release();
            }

            super.onCancelled();
        }
    }

    private void startHFCode() {
        recordAsyncTask = new RecordAsyncTask();
        recordAsyncTask.execute();
    }
}
