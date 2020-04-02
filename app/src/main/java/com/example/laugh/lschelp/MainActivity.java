package com.example.laugh.lschelp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.laugh.lschelp.data.CountTime;
import com.example.laugh.lschelp.receiver.MyBroadcastReceiver;
import com.example.laugh.lschelp.util.StatusBarUtils;

public class MainActivity extends AppCompatActivity implements TimeCallBack {

    private NotificationManager manager;
    private MyBroadcastReceiver myBroadcastReceiver;
    private static final int NOTIFICATION_ID = 2;
    private static final int TIME_ID = 1;

    /**
     * 完成了几次
     */
    private int finishNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通知栏沉浸
        StatusBarUtils.immersive(this);
        setContentView(R.layout.activity_main);

        checkRingerMode();
        setClickListener();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //通知channel
        createNotificationChannel(getString(R.string.channel_time_id), getString(R.string.channel_time_name), NotificationManager.IMPORTANCE_LOW);
        createNotificationChannel(getString(R.string.channel_notification_id), getString(R.string.channel_notification_name), NotificationManager.IMPORTANCE_HIGH);
        createToolbar(CountTime.newInstance());
        initReceiver();
    }

    private void setClickListener(){
        findViewById(R.id.button).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        manager.cancelAll();
    }

    /**
     * 创建通知通道（Android8.0以上需要）
     *
     * @param channelId: 通道ID
     * @param channelName: 通道名字
     * @param important: 通道的等级
     */
    private void createNotificationChannel(String channelId, String channelName, int important) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, important);
        if(important >= NotificationManager.IMPORTANCE_HIGH){
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{0, 500, 500, 500, 500, 500});
            channel.setBypassDnd(true);
        }
        else
            channel.enableVibration(false);
        manager.createNotificationChannel(channel);
    }

    /**
     * 检测是否开启震动
     */
    private void checkRingerMode(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() == 0){
            Toast.makeText(this, "请开启震动", Toast.LENGTH_SHORT).show();
        }
    }

    private void createToolbar(CountTime countTime){
        Intent button2 = new Intent(MyBroadcastReceiver.TWO);
        Intent button3 = new Intent(MyBroadcastReceiver.THREE);
        Intent buttonQ = new Intent(MyBroadcastReceiver.QUIT);
        Intent buttonC = new Intent(MyBroadcastReceiver.CANCEL);

        PendingIntent button2Pi = PendingIntent.getBroadcast(this, 0, button2, 0);
        PendingIntent button3Pi = PendingIntent.getBroadcast(this, 0, button3, 0);
        PendingIntent buttonQPi = PendingIntent.getBroadcast(this, 0, buttonQ, 0);
        PendingIntent buttonCPi = PendingIntent.getBroadcast(this, 0, buttonC, 0);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setOnClickPendingIntent(R.id.two_minute, button2Pi);
        contentView.setOnClickPendingIntent(R.id.three_minute, button3Pi);
        contentView.setOnClickPendingIntent(R.id.quit, buttonQPi);
        contentView.setOnClickPendingIntent(R.id.cancel, buttonCPi);
        contentView.setTextViewText(R.id.time_text, countTime.toTimeMsg(finishNumber));

        // 创建通知
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.channel_time_id))
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(TIME_ID, notification);
    }

    @Override
    public void refreshTime(CountTime countTime) {
        createToolbar(countTime);
    }

    @Override
    public void timeout() {
        ++finishNumber;
        refreshTime(CountTime.newInstance());
        raiseNotification();
    }

    @Override
    public void resetTime() {
        finishNumber = 0;
        manager.cancel(NOTIFICATION_ID);
        refreshTime(CountTime.newInstance());
    }

    @Override
    public void quit() {
        finish();
    }

    private void raiseNotification(){
        Notification notification = new NotificationCompat.Builder(this, getString(R.string.channel_notification_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setContentText(getString(R.string.notification_template, finishNumber))
                .setContentTitle(getString(R.string.channel_notification_name))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .build();
        manager.notify(NOTIFICATION_ID, notification);
    }

    private void initReceiver(){
        myBroadcastReceiver = new MyBroadcastReceiver(MainActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyBroadcastReceiver.TWO);
        intentFilter.addAction(MyBroadcastReceiver.THREE);
        intentFilter.addAction(MyBroadcastReceiver.QUIT);
        intentFilter.addAction(MyBroadcastReceiver.CANCEL);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }
}
