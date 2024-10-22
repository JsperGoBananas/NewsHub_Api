package com.jl.newshubapi;

import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 提交任务并返回 Future 对象
        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(2000); // 模拟长时间计算
            return 42;
        });

        // 可以做其他事情，等任务完成
        System.out.println("等待计算完成...");

        // 获取任务结果（get 会阻塞直到任务完成）
        Integer result = future.get();
        System.out.println("计算结果: " + result);

        executor.shutdown();
    }
}
