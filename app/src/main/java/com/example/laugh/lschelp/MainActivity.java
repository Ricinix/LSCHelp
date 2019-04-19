package com.example.laugh.lschelp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public NotificationManager manager;
    public RemoteViews contentView;
    private MyBroadcastReceiver myBroadcastReceiver;
    private Button quit;
    private AudioManager audioManager;

    public void setFinishNumber(int finishNumber) {
        this.finishNumber = finishNumber;
    }

    public int getFinishNumber() {
        return finishNumber;
    }

    private int finishNumber = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通知栏沉浸
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        quit = (Button) findViewById(R.id.button);
        quit.setOnClickListener(v->{
            finish();
        });
        //通知channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId1 = "time";
            String channelName1 = "计时";
            int important1 = NotificationManager.IMPORTANCE_LOW;
            createNotificationChannel(channelId1, channelName1, important1);

            String channelId2 = "notification";
            String channelName2 = "通知";
            int important2 = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId2, channelName2, important2);
        }
        createtoolbar("0:00");
        initRecevier();
        if (audioManager.getRingerMode() == 0){
            Toast.makeText(this, "请开启震动", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        manager.cancelAll();
    }

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

    public void createtoolbar(String time){
        Intent button2 = new Intent(MyBroadcastReceiver.TWO);
        Intent button3 = new Intent(MyBroadcastReceiver.THREE);
        Intent buttonQ = new Intent(MyBroadcastReceiver.QUIT);
        Intent buttonC = new Intent(MyBroadcastReceiver.CANCEL);

        PendingIntent button2Pi = PendingIntent.getBroadcast(this, 0, button2, 0);
        PendingIntent button3Pi = PendingIntent.getBroadcast(this, 0, button3, 0);
        PendingIntent buttonQPi = PendingIntent.getBroadcast(this, 0, buttonQ, 0);
        PendingIntent buttonCPi = PendingIntent.getBroadcast(this, 0, buttonC, 0);
        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setOnClickPendingIntent(R.id.two_minute, button2Pi);
        contentView.setOnClickPendingIntent(R.id.three_minute, button3Pi);
        contentView.setOnClickPendingIntent(R.id.quit, buttonQPi);
        contentView.setOnClickPendingIntent(R.id.cancel, buttonCPi);
        contentView.setTextViewText(R.id.time_text, String.valueOf(finishNumber) + " - " + time);

        Notification notification = new NotificationCompat.Builder(this, "time")
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(1, notification);
    }

    public void raiseNotification(String content){

        Notification notification;
        notification = new NotificationCompat.Builder(this, "notification")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setContentText(content)
                .setContentTitle("通知")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .build();

        manager.notify(2, notification);
    }

    private void initRecevier(){
        myBroadcastReceiver = new MyBroadcastReceiver(MainActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyBroadcastReceiver.TWO);
        intentFilter.addAction(MyBroadcastReceiver.THREE);
        intentFilter.addAction(MyBroadcastReceiver.QUIT);
        intentFilter.addAction(MyBroadcastReceiver.CANCEL);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }
}
