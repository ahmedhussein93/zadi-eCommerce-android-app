package com.cafateria.services;

import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ahmed on 22/01/2018.
 */
public class Scheduler {
    private static final String TAG = "Scheduler";
    private static Timer mTimer;
    private static TimerTask mTt;
    private static Handler mTimerHandler = new Handler();

    public static void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    interface ScheduleCallBack {
        void call();
    }

    public static void start(int delay_in_minutes, int interval_in_minutes, final ScheduleCallBack scheduleCallBack) {
        mTimer = new Timer();
        Log.e(TAG, "timer started");
        mTt = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        scheduleCallBack.call();
                    }
                });
            }
        };
        mTimer.schedule(mTt, delay_in_minutes * 1000 * 60, interval_in_minutes * 1000 * 60);//interval in minutes
    }
}
