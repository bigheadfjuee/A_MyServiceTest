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

import static java.lang.Thread.sleep;

/**
 * Created by tony on 2017/5/11.
 */

public class MyService extends Service {
    public static final String TAG = "MyService";
    private MyBinder mBinder = new MyBinder();
    private static Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        Log.d(TAG, "MyService thread id is " + android.os.Process.myTid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        Log.d(TAG, "onStartCommand thread id is " + Thread.currentThread().getId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Service 也是跑在 main thread，比較耗時的工作要另外開 thread
                while (true) {
                    try {
                        Log.d(TAG, "doMyWork thread id is " + Thread.currentThread().getId());
                        sleep(10000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
        }).start();

        Intent intentS = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intentS, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Go", pendingIntent).build();

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("MyService")
                .setContentText("running")
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pendingIntent) //Android Wear 有設定這個時，就不會有 addAction 功能
                .addAction(action) // Android Wear 要點一下通知，才會看到此按鈕
                .setDefaults(Notification.DEFAULT_SOUND) // 在 Android Wear - Q Marchal 7769 沒作用
                .build();
        startForeground(R.string.app_name, notification);



        //return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        // 一個 Service 必須在既沒有和任何Activity關聯 又處於停止狀態的時候才會被銷毀。
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return mBinder; // 傳回 binder 給 Activity 用
    }

    class MyBinder extends Binder {

        public void doMyWork() {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            Log.d(TAG, "doMyWork()");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "doMyWork thread id is " + Thread.currentThread().getId());
                    while (true) {
                        Log.d(TAG, "run");
//                        vibrator.vibrate(800); // 震動
                        try {
                            Log.d(TAG, "doMyWork thread id is " + Thread.currentThread().getId());
                            sleep(10000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }

    }

}
