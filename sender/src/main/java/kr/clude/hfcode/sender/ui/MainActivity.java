package kr.clude.hfcode.sender.ui;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import kr.clude.hfcode.sender.R;
import kr.clude.hfcode.sender.util.AudioTrackGenerator;

import static kr.clude.hfcode.base.Config.SAMPLE_RATE;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecordAsyncTask recordAsyncTask;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        editText = (EditText) findViewById(R.id.et);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        startHFCode();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recordAsyncTask != null) {
            recordAsyncTask.cancel(true);
        }
    }

    private void startHFCode() {

        int data = 0;
        try {
            data = Integer.parseInt(editText.getText().toString());
            Log.d(TAG, "data -> " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        recordAsyncTask = new RecordAsyncTask();
        recordAsyncTask.execute(data);
    }


    public class RecordAsyncTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            short[] mBuffer = AudioTrackGenerator.generate(params[0], 1);

            // AudioTrack definition
            int mBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_8BIT);

            AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    mBufferSize,
                    AudioTrack.MODE_STREAM);

            mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
            mAudioTrack.play();
            mAudioTrack.write(mBuffer, 0, mBuffer.length);
            mAudioTrack.stop();
            mAudioTrack.release();

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... data) {

        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

            startHFCode();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
