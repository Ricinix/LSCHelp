package com.example.laugh.lschelp.data;

public class CountTime {
    private int minute;
    private int second;

    private CountTime(int minute, int second){
        this.minute = minute;
        this.second = second;
    }

    public void setTIme(int minute, int second){
        this.minute = minute;
        this.second = second;
    }

    public void forward(){
        if (second > 0) --second;
        else{
            second = 59;
            --minute;
        }
    }

    public boolean isTimeOut(){
        return second == 0 && minute == 0;
    }

    public String toTimeMsg(int finishTimes){
        StringBuilder sb = new StringBuilder();
        sb.append(finishTimes);
        sb.append(" - ");
        sb.append(toTimeMsg());
        return sb.toString();
    }

    public String toTimeMsg(){
        StringBuilder sb = new StringBuilder();
        sb.append(minute);
        sb.append(":");
        if (second < 10) sb.append('0');
        sb.append(second);
        return sb.toString();
    }

    public static CountTime valueOf(int minute, int second){
        return new CountTime(minute, second);
    }

    public static CountTime valueOf(String time){
        String[] times = time.split(":");
        if (times.length != 2){
            throw new IllegalArgumentException();
        }
        return new CountTime(Integer.parseInt(times[0]), Integer.parseInt(times[1]));
    }

    public static CountTime newInstance(){
        return valueOf(0, 0);
    }
}
