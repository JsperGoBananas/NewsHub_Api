package com.jl.newshubapi;

import java.util.concurrent.*;

public class CompletableFutureExample {
    public static void main(String[] args) {
        // 创建异步任务
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // 模拟长时间计算
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        });

        // 异步回调，当任务完成后自动执行
        future.thenAccept(result -> System.out.println("计算结果: " + result));

        // 主线程继续执行其他任务，不被阻塞
        System.out.println("等待计算完成...");

        // 防止程序过早退出
        future.join();
    }
}
