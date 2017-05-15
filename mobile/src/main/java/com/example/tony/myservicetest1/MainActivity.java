package com.example.tony.myservicetest1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    private Button btnStartS;
    private Button btnStopS;
    private Button btnBindS;
    private Button btnUnbindS;
    private Button btnTest;
    private MyService.MyBinder myBinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Process.myPid(): " + android.os.Process.myPid());
        Log.d(TAG, "Process.myTid(): " + android.os.Process.myTid());
        Log.d(TAG, "MainActivity thread id is " + Thread.currentThread().getId());

        btnStartS = (Button) findViewById(R.id.start_service);
        btnStopS = (Button) findViewById(R.id.stop_service);
        btnBindS = (Button) findViewById(R.id.bind_service);
        btnUnbindS = (Button) findViewById(R.id.unbind_service);
        btnTest = (Button) findViewById(R.id.btn_test);
        btnStartS.setOnClickListener(this);
        btnStopS.setOnClickListener(this);
        btnBindS.setOnClickListener(this);
        btnUnbindS.setOnClickListener(this);
        btnTest.setOnClickListener(this);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            myBinder = (MyService.MyBinder) service; // 取得 Service 的 Binder
            myBinder.doMyWork(100); // 使用 Binder 的 method
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                Log.d(TAG, "click Start Service button");
                Intent startIntent = new Intent(this, MyService.class); // this 等同 getApplicationContext()
                startIntent.putExtra("KEY1", "Value to My Service");
                startService(startIntent); // getApplicationContext().btnStartS(i);
                break;

            case R.id.stop_service:
                Log.d(TAG, "click Stop Service button");
                Intent stopIntent = new Intent(this, MyService.class);
                stopService(stopIntent);
                break;

            case R.id.bind_service:
                Log.d(TAG, "click Bind Service button");
                Intent bindIntent = new Intent(this, MyService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);
                // BIND_AUTO_CREATE => MyService 中的 onCreate()方法會執行，但onStartCommand()方法不會執行
                break;

            case R.id.unbind_service:
                Log.d(TAG, "click Unbind Service button");
                unbindService(connection);
                break;

            case R.id.btn_test:
                TestMyNofity();
                break;

            default:
                break;
        }
    }

    public void TestMyNofity() {
        Intent intentS = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 1, intentS, PendingIntent.FLAG_UPDATE_CURRENT);

//Button
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Go", pendingIntent).build();

// 後來要用 NotificationCompat.Builder 才會有作用
        Notification MyNotification = new NotificationCompat.Builder(this)
                .setContentTitle("MyTitle")
                .setContentText("MyText")
                .setSmallIcon(R.mipmap.ic_launcher) // 必備，不然通知不會出現
                .setContentIntent(pendingIntent)
                .addAction(action)
                .setTicker("Ticker") // Android5.0.2 API21 之後沒作用了
//                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(false) // true:按下後會直接消失
                .setNumber(9) // 控制通知數量
                .setVibrate(new long[]{0, 2000, 800, 2000}) // 震動
                .build();

        NotificationManager gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        gNotMgr.cancelAll();
        gNotMgr.notify(1, MyNotification);

    }

}
