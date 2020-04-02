package com.example.laugh.lschelp;

import com.example.laugh.lschelp.data.CountTime;

public interface TimeCallBack {
    void refreshTime(CountTime countTime);
    void timeout();
    void resetTime();
    void quit();
}
