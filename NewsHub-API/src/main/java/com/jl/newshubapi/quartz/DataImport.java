package com.jl.newshubapi.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jl.newshubapi.model.entity.Website;
import com.jl.newshubapi.service.ArticleService;
import com.jl.newshubapi.service.IWebsiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DataImport {
    @Autowired
    ArticleService articleService;

    @Autowired
    IWebsiteService websiteService;
    //定时任务，每隔三十分钟执行一次
    // 用多线程同步
     @Scheduled(cron = "0 0/30 * * * ?")
    public void importData() {

//         //统计用时
//            long startTime = System.currentTimeMillis();
//         List<Website> list = websiteService.list(new QueryWrapper<Website>().eq("is_rss",true));
//         for (Website website : list) {
//             try{
//                 articleService.saveOnlineArticle(website.getFetchDataUrl());
//             }catch (RuntimeException e){
//                 log.error(website.getTitle()+"数据同步失败",e);
//             }
//
//         }
//         List<Website> list1 = websiteService.list(new QueryWrapper<Website>().eq("is_rss",false));
//         for (Website website : list1) {
//             try{
//                 articleService.saveOnlineArticle(website.getFetchDataUrl(),website.getId() );
//             }catch (RuntimeException e){
//                 log.error(website.getTitle()+"数据同步失败",e);
//             }
//
//         }
//            long endTime = System.currentTimeMillis();
//            log.info("数据同步完成，用时："+(endTime-startTime)+"ms");

         // 统计用时
         long startTime = System.currentTimeMillis();

         // 创建一个固定大小的线程池
         ExecutorService executorService = Executors.newFixedThreadPool(10);

         // 处理 RSS 站点的数据同步
         List<Website> rssWebsites = websiteService.list(new QueryWrapper<Website>().eq("is_rss", true));
         for (Website website : rssWebsites) {
             executorService.submit(() -> {
                 try {
                     articleService.saveOnlineArticle(website.getFetchDataUrl());
                 } catch (RuntimeException e) {
                     log.error(website.getTitle() + " 数据同步失败", e);
                 }
             });
         }

         // 处理非 RSS 站点的数据同步
         List<Website> nonRssWebsites = websiteService.list(new QueryWrapper<Website>().eq("is_rss", false));
         for (Website website : nonRssWebsites) {
             executorService.submit(() -> {
                 try {
                     articleService.saveOnlineArticle(website.getFetchDataUrl(), website.getId());
                 } catch (RuntimeException e) {
                     log.error(website.getTitle() + " 数据同步失败", e);
                 }
             });
         }

         // 关闭线程池并等待所有任务完成
         executorService.shutdown();
         try {
             // 等待所有任务在指定时间内完成
             if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                 executorService.shutdownNow(); // 超时则强制关闭
             }
         } catch (InterruptedException e) {
             executorService.shutdownNow();
             Thread.currentThread().interrupt(); // 重新设置线程中断状态
         }

         long endTime = System.currentTimeMillis();
         log.info("数据同步完成，用时：" + (endTime - startTime) + "ms");

    }


}
