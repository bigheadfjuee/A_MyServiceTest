package com.example.tony.myservicetest1;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ViewDebug;

import static java.lang.Thread.sleep;

/**
 * Created by tony on 2017/5/11.
 */

public class MyService extends Service {
    public static final String TAG = "MyService";
    private MyBinder mBinder = new MyBinder();
    private static boolean isDoMyWork;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        Log.d(TAG, "Process.myPid(): " + android.os.Process.myPid());
        Log.d(TAG, "MyService thread id is " + Thread.currentThread().getId());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent intentS = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intentS, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Go", pendingIntent).build();
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("MyService")
                .setContentText("running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .addAction(action)
                .build();
        startForeground(1, notification);

        Log.d(TAG, "onStartCommand()");
        //return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        mBinder.stopMyWork();
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return mBinder;  // 和 Activity 通訊用的 Binder
    }

    class MyBinder extends Binder {

        public void stopMyWork()
        {
            Log.d(TAG, "stopMyWork");
            isDoMyWork = false;
        }

        public void doMyWork(final int value) {
            Log.d(TAG, "doMyWork(): " + String.valueOf(value));

            isDoMyWork = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = value;
                    // 因為 Service 也是用 main thread，要處理複雜的事，可另開 thread
                    Log.d(TAG, "doMyWork thread id is " + Thread.currentThread().getId());

                    while (isDoMyWork) {
                        try {
                            Log.d(TAG, "doMyWork run count: " + String.valueOf(count));
                            count++;
                            sleep(10000); // 10 秒

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

    }

}
