package com.dzf.zxkj.base.framework.util;


public class ReadWriteLock {
    /**
     * 保存锁的获取优先参数
     * 默认是读锁优先。
     * 读锁优先时候，也就是当锁释放时，有读线程在等待获取锁，一个写线程在等待获取锁，那某个读线程获得锁
     */
    private boolean readLockFirst = true;
    /**
     * 读线程计数器:获取了读锁的线程数量
     **/
    private int readingThreadCount = 0;

    /***
     * 写线程计数器
     * **/
    private int writeThreadCount = 0;

    public ReadWriteLock(boolean readLockFirst) {
        this.readLockFirst = readLockFirst;
    }

    public ReadWriteLock() {
        readLockFirst = true;
    }

    /**
     * 获取读锁
     **/
    public synchronized void readLock() {
        try {
            while (!readLockFirst && writeThreadCount > 0) {
                wait();
            }
        } catch (InterruptedException e) {
        }
        readingThreadCount++;
    }

    /**
     * 释放读锁
     **/
    public synchronized void readUnLock() {
        readingThreadCount--;
        if (readingThreadCount == 0) {
            notifyAll();
        }
    }

    /**
     * 获取写锁
     **/
    public synchronized void writeLock() {
        try {
            while (readingThreadCount > 0 || writeThreadCount > 0) {
                wait();
            }
        } catch (InterruptedException e) {
        }
        writeThreadCount++;
    }

    /**
     * 释放写锁
     **/
    public synchronized void writeUnLock() {
        writeThreadCount--;
        notifyAll();
    }

    public static void main(String[] args) {
        //ReadWriteLock lock = new ReadWriteLock();
        //使用读锁例子
        //public void readData() {
        // lock.readLock();
        //doRead();
        //  lock.readUnLock();
        // }

        //使用写锁例子
        // public void writeData() {
        //  lock.writeLock();
        //  doWrite();
        //  lock.writeUnLock();
        //}
    }
}

