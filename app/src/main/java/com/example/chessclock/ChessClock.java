package com.example.chessclock;

public class ChessClock {
    private long timeOnClock;
    private boolean isRunning;
    private boolean isFinished;

    public ChessClock(long timeOnClock){
        this.timeOnClock = timeOnClock;
    }

    public long getTimeOnClock(){
        return timeOnClock;
    }

    public void setTimeOnClock(long time){
        this.timeOnClock = time;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void setRunning(boolean running){
        this.isRunning = running;
    }

    public boolean isFinished(){
        return isFinished;
    }

    public void setFinished(boolean finished){
        this.isFinished = finished;
    }
}
