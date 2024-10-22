package com.jl.newshubapi;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThread {

    private static int count = 0;
    private static Object lock = new Object();

    Lock lock1 = new ReentrantLock();
    public static void main(String[] args) {
        new Thread(()->{
            while(count < 100){
                synchronized(lock){
                    while(count %2 != 0){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("A");
                    count++;
                    lock.notifyAll();
                }
            }
        }).start();
        new Thread(()->{
            while(count < 100){
                synchronized(lock){
                    while(count %2 != 1){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("B");
                    count++;
                    lock.notifyAll();
                }
            }
        }).start();
    }
}



