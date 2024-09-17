package com.jl.newshubapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jl.newshubapi.model.entity.Website;
import com.jl.newshubapi.quartz.DataImport;
import com.jl.newshubapi.service.ArticleService;
import com.jl.newshubapi.service.IWebsiteService;
import com.jl.newshubapi.utils.RSSUtil;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
class HotListApiApplicationTests {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private IWebsiteService websiteService;

    @Autowired
    private DataImport dataImport;
    @Test
    void contextLoads() {
    }


//    @Test
//    void test(){
//                try {
//                    // RSS Feed的URL
//                    String rssUrl = "https://jaredtao.github.io/atom.xml";
//
//                    // 解析RSS Feed
//                    URL feedUrl = new URL(rssUrl);
//                    SyndFeedInput input = new SyndFeedInput();
//                    SyndFeed feed = input.build(new XmlReader(feedUrl));
//
//                    // 打印Feed的标题和描述
//                    System.out.println("Feed Title: " + feed.getTitle());
//                    System.out.println("Feed Link: " + feed.getLink());
//                    System.out.println("Feed Description: " + feed.getDescription());
//                    System.out.println("Feed Published Date: " + feed.getPublishedDate());
//                    System.out.println("Feed Last Build Date: " + feed.getPublishedDate());
//
//                    // 打印Feed的图片信息
//                    if (feed.getImage() != null) {
//                        System.out.println("Feed Image URL: " + feed.getImage().getUrl());
//                        System.out.println("Feed Image Title: " + feed.getImage().getTitle());
//                        System.out.println("Feed Image Link: " + feed.getImage().getLink());
//                    }
//
//                    System.out.println();
//
//                    // 打印每个条目的标题和链接
//                    for (SyndEntry entry : feed.getEntries()) {
//                        System.out.println("Title: " + entry.getTitle());
//                        System.out.println("Link: " + entry.getLink());
//                        System.out.println("Published Date: " + entry.getPublishedDate());
//                        System.out.println();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

    @Test
    void testSaveOnlineArticle(){

    }



    @Test
    void testGetArticleList(){
//        articleService.saveOnlineArticle(TOUTIAO_NEWS_URL, DataSource.TOUTIAO_NEWS);
//        articleService.saveOnlineArticle(TENCENT_NEWS_URL, DataSource.TENCENT_NEWS);
//        articleService.saveOnlineArticle(BBC_URL, DataSource.BBC_NEWS);
//        articleService.saveOnlineArticle(GITHUB_URL, DataSource.GITHUB);
//        articleService.saveOnlineArticle(NY_TIMES_URL, DataSource.NY_TIMES);
//        articleService.saveOnlineArticle(SSPAI_URL, DataSource.SSPAI);
//        articleService.saveOnlineArticle(ZHIHU_URL, DataSource.ZHIHU);
//        articleService.saveOnlineArticle(CCTV_URL, DataSource.CCTV_NEWS);
//        articleService.saveOnlineArticle(WIKIPEDIA_ZH_URL+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), DataSource.WIKIPEDIA_MOST_READ_ZH);
//        articleService.saveOnlineArticle(WIKIPEDIA_EN_URL+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), DataSource.WIKIPEDIA_HISTORY_EN);
//        articleService.saveOnlineArticle(WIKIPEDIA_EN_URL+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")), DataSource.WIKIPEDIA_MOST_READ_EN);
//        articleService.saveOnlineArticle(ZHIHU_DAILY_URL, DataSource.ZHIHU_DAILY);
//        articleService.saveOnlineArticle(RequestType.ThirtySixKr.createRequest(), DataSource.THIRTY_SIX_KR);
//        articleService.saveOnlineArticle(CTO51_URL, DataSource.CTO51);
//        articleService.saveOnlineArticle(TENCENT_NEWS_URL, DataSource.TENCENT_NEWS);
//        articleService.saveOnlineArticle(TOUTIAO_NEWS_URL, DataSource.TOUTIAO_NEWS);
        Website website = websiteService.getOne(new QueryWrapper<Website>().eq("id", "1"));
        articleService.saveOnlineArticle(website.getFetchDataUrl(),website.getId());
    }

    @Test
    void testGetArticleListBySource(){
//        List<Website> list = websiteService.list(new QueryWrapper<Website>().eq("is_rss",true));
//        for (Website website : list) {
//            try{
//                articleService.saveOnlineArticle(website.getFetchDataUrl());
//            }catch (RuntimeException e){
//                e.printStackTrace();
//            }
//
//        }
//        List<Website> list1 = websiteService.list(new QueryWrapper<Website>().eq("is_rss",false));
//        for (Website website : list1) {
//            try{
//                articleService.saveOnlineArticle(website.getFetchDataUrl(),website.getId() );
//            }catch (RuntimeException e){
//                e.printStackTrace();
//            }
//
//        }
//        Website id = websiteService.getOne(new QueryWrapper<Website>().eq("id", "31"));
//        articleService.saveOnlineArticle(id.getFetchDataUrl());
        List<Website> list = websiteService.list(new QueryWrapper<Website>().eq("is_rss",true));
        for (Website website : list) {
            try{
                articleService.saveOnlineArticle(website.getFetchDataUrl());
            }catch (RuntimeException e){
//                log.error(website.getTitle()+"数据同步失败",e);
            }

        }
        List<Website> list1 = websiteService.list(new QueryWrapper<Website>().eq("is_rss",false));
        for (Website website : list1) {
            try{
                articleService.saveOnlineArticle(website.getFetchDataUrl(),website.getId() );
            }catch (RuntimeException e){
//                log.error(website.getTitle()+"数据同步失败",e);
            }

        }

    }

    @Test
    void testGetArticleListBySource1(){

        List<Website> list = websiteService.list(new QueryWrapper<Website>().like("source", "rss_"));
        for (Website website : list) {
            articleService.saveOnlineArticle(website.getFetchDataUrl());
        }
    }

    @Test
    void testgetRssFeed(){

                try {
                    // RSS Feed的URL
                    String rssUrl = "https://www.zhihu.com/rss"; // 将其替换为你想要读取的RSS URL

                    // 使用ROME库从URL读取RSS feed
                    URL feedUrl = new URL(rssUrl);
                    SyndFeedInput input = new SyndFeedInput();
                    SyndFeed feed = input.build(new XmlReader(feedUrl));

                    // 打印频道标题和描述
                    System.out.println("Feed Title: " + feed.getTitle());
                    System.out.println("Feed Description: " + feed.getDescription());
                    System.out.println(feed.getImage().getLink());
                    System.out.println();

                    // 获取所有内容项
                    List<SyndEntry> entries = feed.getEntries();
                    for (SyndEntry entry : entries) {
                        System.out.println("Title: " + entry.getTitle());
                        System.out.println("Link: " + entry.getLink());
                        System.out.println("Published Date: " + entry.getPublishedDate().getTime());
                        System.out.println("Description: " + entry.getDescription().getValue());
                        System.out.println();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

    }

    @Test
    void testGetArticleListBySource2(){
//        System.out.println(RSSUtil.getBaseUrl("https://www.ifanr.com?utm_source=rss&utm_medium=rss&utm_campaign="));
        //开十个线程调用这个方法
        dataImport.importData();
    }




}
