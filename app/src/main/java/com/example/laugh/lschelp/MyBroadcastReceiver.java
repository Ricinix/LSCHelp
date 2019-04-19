package com.example.laugh.lschelp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String TWO = "pressTwo";
    public static final String THREE = "pressThree";
    public static final String QUIT = "pressQuit";
    public static final String CANCEL = "pressCancel";
    private Thread timing;
    private MainActivity activity;
    public MyBroadcastReceiver(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(TWO)){
            if(timing != null && timing.isAlive())
                timing.interrupt();
            timing = new Thread(()->{
                int count = 120;
                activity.createtoolbar("2:00");
                while(count>0){
                    try {
                        timing.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    if(timing.isInterrupted())
                        break;
                    count--;
                    String minute;
                    String second;
                    minute = String.valueOf(count/60);
                    second = String.valueOf(count%60);
                    if (second.length() == 1)
                        second = "0" + second;
                    activity.createtoolbar(minute+":"+second);
                }
                if(count == 0){
                    activity.setFinishNumber(activity.getFinishNumber() + 1);
                    activity.raiseNotification(
                            "第" + String.valueOf(activity.getFinishNumber()) + "次已完成");
                }
            });
            timing.start();
        }
        else if(action.equals(THREE)){
            if(timing != null && !timing.isInterrupted())
                timing.interrupt();
            timing = new Thread(()->{
                int count = 180;
                activity.createtoolbar("3:00");
                while(count>0){
                    try {
                        timing.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    if(timing.isInterrupted())
                        break;
                    count--;
                    String minute;
                    String second;
                    minute = String.valueOf(count/60);
                    second = String.valueOf(count%60);
                    if (second.length() == 1)
                        second = "0" + second;
                    activity.createtoolbar(minute+":"+second);
                }
                if(count == 0){
                    activity.setFinishNumber(activity.getFinishNumber() + 1);
                    activity.raiseNotification(
                            "第" + String.valueOf(activity.getFinishNumber()) + "次已完成");
                }

            });
            timing.start();
        }
        else if(action.equals(CANCEL)){
            if(timing != null && !timing.isInterrupted())
                timing.interrupt();
            activity.setFinishNumber(0);
            activity.manager.cancel(2);
            activity.createtoolbar("0:00");
        }
        else if(action.equals(QUIT)){
            if(timing != null && !timing.isInterrupted())
                timing.interrupt();
            ((Activity)context).finish();
        }
    }
}
