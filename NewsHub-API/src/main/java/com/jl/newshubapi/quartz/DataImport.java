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

@Service
@Slf4j
public class DataImport {
    @Autowired
    ArticleService articleService;

    @Autowired
    IWebsiteService websiteService;
    //定时任务，每隔三十分钟执行一次
     @Scheduled(cron = "0 0/30 * * * ?")
    public void importData() {
         //统计用时
            long startTime = System.currentTimeMillis();
         List<Website> list = websiteService.list(new QueryWrapper<Website>().eq("is_rss",true));
         for (Website website : list) {
             try{
                 articleService.saveOnlineArticle(website.getFetchDataUrl());
             }catch (RuntimeException e){
                 log.error(website.getTitle()+"数据同步失败",e);
             }

         }
         List<Website> list1 = websiteService.list(new QueryWrapper<Website>().eq("is_rss",false));
         for (Website website : list1) {
             try{
                 articleService.saveOnlineArticle(website.getFetchDataUrl(),website.getId() );
             }catch (RuntimeException e){
                 log.error(website.getTitle()+"数据同步失败",e);
             }

         }
            long endTime = System.currentTimeMillis();
            log.info("数据同步完成，用时："+(endTime-startTime)+"ms");

    }


}
