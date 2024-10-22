package com.jl.newshubapi;

import java.util.concurrent.*;

public class CompletableFutureCombineExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        // 任务1
//        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
//
//        // 任务2
//        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);
//
//        // 合并任务1和任务2的结果
//        CompletableFuture<Integer> result = future1.thenCombine(future2, (a, b) -> a + b);
//
//        // 输出合并结果
//        result.thenAccept(System.out::println); // 输出: 30

        // 防止程序过早退出
//        result.join();

        CompletableFuture<Integer> f1= CompletableFuture.supplyAsync(()->10);

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(()    -> 20);

        CompletableFuture<Integer> result = f1.thenCombine(f2,(a,b)->a-b);

        result.thenAccept(System.out::println);
    }
}
