package com.dzf.zxkj.base.framework.util;


public class StopWatch {

    /**
     * time conversion constants
     */
    public static final long NUMBER_OF_MILLSECS_IN_SECOND = 1000;
    public static final long NUMBER_OF_SECONDS_IN_MINUTE = 60;
    public static final long NUMBER_OF_MINUTES_IN_HOUR = 60;
    public static final long NUMBER_OF_SECONDS_IN_HOUR = 3600;
    public static final long NUMBER_OF_HOURS_IN_DAY = 24;

    /**
     * The times at which this watch object is started and stopped
     */
    private long startTime;
    private long stopTime;

    /**
     * true if the stop watch is currently running, false otherwise
     */
    private boolean isRunning;

    /**
     * Constructor requiring no arguments
     */
    public StopWatch() {
        reset();
    }

    /**
     * Return the watch to its initial state
     */
    public void reset() {
        startTime = stopTime = 0;
        isRunning = false;
    }

    /**
     * Start the watch counting time.
     */
    public void start() {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        isRunning = true;
    }

    /**
     * Stop the watch from counting time.
     */
    public void stop() {
        stopTime = System.currentTimeMillis();
        isRunning = false;
    }

    /**
     * Return the elapsed time in milliseconds.
     *
     * @return the number of miliseconds between start and stop time if the
     * watch is stopped or between the start and current time if the
     * watch is running
     */
    public long getElapsedTime() {
        if (isRunning == true) {
            long timeNow = System.currentTimeMillis();
            return timeNow - startTime;
        } else {
            return stopTime - startTime;
        }
    }
}

