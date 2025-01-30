package com.jl.newshubapi;

import com.alibaba.fastjson.JSON;
import com.jl.newshubapi.model.entity.*;
import com.jl.newshubapi.service.ArticleService;
import com.jl.newshubapi.service.IAiSummaryService;
import com.jl.newshubapi.service.impl.AiSummaryServiceImpl;
import com.jl.newshubapi.utils.RequestUtil;
import com.jl.newshubapi.utils.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest

public class ZhipuTest {

    @Autowired
    private AiSummaryServiceImpl aiSummaryService;

    @Autowired
    private IAiSummaryService iAiSummaryService;

    @Autowired
    private ArticleService articleService;
    @Test
    public void generateMessage() {
        Message message = new Message();
        message.setRole("user");
        message.setContent("content");
        assert message.getRole().equals("user");
        assert message.getContent().equals("content");
    }

    @Test
    public void generateMessage2() {
        Message message = new Message();
        message.setRole("user");
        message.setContent("content");
        assert message.getRole().equals("user");
        assert message.getContent().equals("content");
    }

    @Test
    public void generateRequestModel(){
        Message message = new Message();
        message.setRole("user");
        message.setContent("content");
        List<Message> list = List.of(message);
        RequestModel requestModel = RequestModel.builder().model("model").messages(list).type("type").build();
        assert requestModel.getModel().equals("model");
        assert requestModel.getMessages().get(0) == message;
        assert requestModel.getType().equals("type");
    }

    @Test
    public void testGetAISUmmary(){
        // 创建 ContentData 实例
        ContentData contentData = new ContentData();

        // 创建 ContentPart 实例
        ContentData.ContentPart contentPart = new ContentData.ContentPart();

        // 创建 Part 实例，并设置内容
        ContentData.Part part = new ContentData.Part();
        List<Article> last12HoursArticles = articleService.getLast12HoursArticles(1);
        List<String> collect = last12HoursArticles.stream().map(Article::getDescription).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(collect));
        part.setText("用中文总结并按类型和重要性生成一篇摘要 返回HTML格式: "+collect);

        // 将 Part 实例加入到 ContentPart 的 parts 列表
        List<ContentData.Part> parts = new ArrayList<>();
        parts.add(part);
        contentPart.setParts(parts);

        // 将 ContentPart 实例加入到 ContentData 的 contents 列表
        List<ContentData.ContentPart> contents = new ArrayList<>();
        contents.add(contentPart);
        contentData.setContents(contents);


//        String aiSummaryResult = aiSummaryService.getAISummary(1);
//        System.out.println(aiSummaryResult);
//        String result = JSON.parseObject(aiSummaryResult).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
////        System.out.println(aiSummaryResult);
//        System.out.println();
//        AiSummary aiSummary = new AiSummary();
//        aiSummary.setGeneratedTime(TimeUtil.getCurrentUTCTime());
//        aiSummary.setSummaryContent(result);
//        aiSummary.setSource(1);
//        iAiSummaryService.save(aiSummary)   ;
    }

    @Test
    public void testget12HoursArticles() {

    }

    @Test
    public void testJsoupScrapeWebsite() throws IOException {
        String url = "https://www.bbc.com/news/articles/c9qjy4lzqn3o";
        List<String> urls = new ArrayList<>();
        urls.add(url);
//        urls.add("https://www.bbc.com/news/videos/czxky4xvg6no");
//        urls.add("https://www.bbc.com/news/articles/cj482l0kw7qo");
//        urls.add("https://www.bbc.com/news/articles/cpwx7qeeyleo");
//        urls.add("https://www.bbc.com/news/articles/c2l0krlpdl2o");
        for (String u : urls) {
            Document doc = Jsoup.connect(u).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Referer", "https://www.bloomberg.com/")
                    .header("Connection", "keep-alive")
                    .ignoreContentType(true)  // 允许获取非HTML内容
                    .timeout(10000).get();

            System.out.println(doc.text());
        }
    }
}
