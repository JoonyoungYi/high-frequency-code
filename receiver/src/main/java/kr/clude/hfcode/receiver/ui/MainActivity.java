package kr.clude.hfcode.receiver.ui;

import android.media.AudioRecord;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import kr.clude.hfcode.receiver.R;
import kr.clude.hfcode.receiver.util.AudioRecordFinder;
import kr.clude.hfcode.receiver.util.fftpack.RealDoubleFFT;

import static kr.clude.hfcode.base.Config.FREQUENCY_WINDOW;
import static kr.clude.hfcode.base.Config.MAX_MESSAGE_INDEX;
import static kr.clude.hfcode.base.Config.MAX_FREQUENCY;
import static kr.clude.hfcode.base.Config.MIN_FREQUENCY;
import static kr.clude.hfcode.base.Config.SAMPLE_RATE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AudioRecord audioRecord;

    private RecordAsyncTask recordAsyncTask;

    private RealDoubleFFT transformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        transformer = new RealDoubleFFT(SAMPLE_RATE);

        recordAsyncTask = new RecordAsyncTask();
        recordAsyncTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recordAsyncTask != null) {
            recordAsyncTask.cancel(true);
        }
    }

    public class RecordAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            audioRecord = AudioRecordFinder.find();
            int bufferSize = SAMPLE_RATE;

            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            audioRecord.read(buffer, 0, bufferSize); // record data from mic into buffer


            //
            double[] toTransform = new double[bufferSize];
            for (int i = 0; i < bufferSize; i++) {
                toTransform[i] = (double) buffer[i] / Short.MAX_VALUE; // signed 16 bit
            }

            transformer.ft(toTransform);

            Log.d(TAG, ">>");

            int window = (int) (2.0 * FREQUENCY_WINDOW);
//            Log.d(TAG, "window -> " + window);

            int data = -1;
            double min_value = 0;
            for (int i = 0; i < MAX_MESSAGE_INDEX; i++) {
                int frequency = (int) (MIN_FREQUENCY + (MAX_FREQUENCY - MIN_FREQUENCY) * i / MAX_MESSAGE_INDEX);
                int standard_index = SAMPLE_RATE * 2 - (int) (4.0 * frequency);
//                Log.d(TAG, "frequency -> " + frequency);
//                Log.d(TAG, "standard_index -> " + standard_index);


                double value = 0;
                for (int j = standard_index - window; j < standard_index + window; j++) {
                    value += toTransform[j];
                }

                Log.d(TAG, "value -> " + value);

                if (min_value > value) {
                    min_value = value;
                    data = i;
                }
            }

            if (data != -1) {
                publishProgress(data + 1);
            }
            audioRecord.release();

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... data) {
            Log.d(TAG, "data -> " + data[0]);
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            audioRecord.release();
        }

    }

}
