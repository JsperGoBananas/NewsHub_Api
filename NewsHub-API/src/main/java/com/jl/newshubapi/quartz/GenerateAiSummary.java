package com.jl.newshubapi.quartz;

import com.alibaba.fastjson.JSON;
import com.jl.newshubapi.model.entity.AiSummary;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.model.entity.Website;
import com.jl.newshubapi.service.ArticleService;
import com.jl.newshubapi.service.IAiSummaryService;
import com.jl.newshubapi.service.IWebsiteService;
import com.jl.newshubapi.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class GenerateAiSummary {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private IWebsiteService websiteService;

    @Autowired
    private IAiSummaryService aiSummaryService;
    //每天早晨6点,晚上6点生成一次
//    @Scheduled(cron = "0 0 6,18 * * ?")
    @Scheduled(cron = "0 0/5 * * * ?")
    public void generateSummary() {
        List<Integer> sources = getSources();
        for (Integer source : sources) {
            String aiSummary = aiSummaryService.getAISummary(source);
            if(aiSummary == null || aiSummary.contains("请")) {
                aiSummary = "暂无数据";
            }
            AiSummary summary = buildSummary(source, aiSummary);
            aiSummaryService.save(summary);
        }
    }

    private AiSummary buildSummary(Integer source, String summary) {
        AiSummary aiSummary = new AiSummary();
        aiSummary.setSource(source);
        aiSummary.setSummaryContent(summary);
        aiSummary.setGeneratedTime(TimeUtil.getCurrentUTCTime());
        return aiSummary;
    }

    private List<Integer> getSources() {
        return websiteService.getWebsiteIdList();
    }


    //根据给的source,获取过去十二个小时内的文章列表
    private List<Article> getlast12HoursArticlesBySource(Integer source) {
        return articleService.getLast12HoursArticles(source);
    }
}
