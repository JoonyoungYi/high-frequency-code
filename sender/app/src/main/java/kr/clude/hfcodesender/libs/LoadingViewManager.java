package kr.clude.hfcodesender.libs;

import android.app.Activity;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by joonyoung.yi on 2017. 6. 4..
 */

public class LoadingViewManager {

    private View progressBar;

    private Timer timer = null;
    private Activity activity;

    public LoadingViewManager(Activity activity, View progressBar) {
        this.progressBar = progressBar;
        this.activity = activity;
    }

    public void showProgress(boolean show) {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (show) {
            this.progressBar.setVisibility(View.VISIBLE);
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                timer = null;
            }
        };

        timer = new Timer();
        timer.schedule(task, 7000);
    }
}
