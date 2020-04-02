package com.example.laugh.lschelp.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.laugh.lschelp.TimeCallBack;
import com.example.laugh.lschelp.data.CountTime;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String TWO = "pressTwo";
    public static final String THREE = "pressThree";
    public static final String QUIT = "pressQuit";
    public static final String CANCEL = "pressCancel";
    private Thread timing;
    private TimeCallBack callBack;

    public MyBroadcastReceiver(TimeCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case TWO:
                startCounting(CountTime.valueOf(2, 0));
                break;
            case THREE:
                startCounting(CountTime.valueOf(3, 0));
                break;
            case CANCEL:
                if (timing != null && !timing.isInterrupted())
                    timing.interrupt();
                callBack.resetTime();
                break;
            case QUIT:
                if (timing != null && !timing.isInterrupted())
                    timing.interrupt();
                callBack.quit();
                break;
        }
    }

    /**
     * 开始计数
     * @param countTime： 设置的时间
     */
    private void startCounting(CountTime countTime) {
        if (timing != null && timing.isAlive())
            timing.interrupt();
        timing = new Thread(() -> {
            callBack.refreshTime(countTime);
            while (!countTime.isTimeOut()) {
                if (timing.isInterrupted())
                    break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                countTime.forward();
                callBack.refreshTime(countTime);
            }
            // 防止撤销线程导致完成通知弹出
            if (countTime.isTimeOut()) {
                callBack.timeout();
            }
        });
        timing.start();
    }
}
