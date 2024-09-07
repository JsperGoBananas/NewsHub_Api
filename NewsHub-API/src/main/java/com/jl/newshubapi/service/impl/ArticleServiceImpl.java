package com.jl.newshubapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jl.newshubapi.constants.enums.AppHttpCodeEnum;
import com.jl.newshubapi.converter.DataConverter;
import com.jl.newshubapi.converter.DataConverterFactory;
import com.jl.newshubapi.converter.RssConverter;
import com.jl.newshubapi.mapper.ArticleMapper;
import com.jl.newshubapi.model.requests.Request;
import com.jl.newshubapi.model.ariticle.vos.HotListVo;
import com.jl.newshubapi.model.dtos.NewsListDto;
import com.jl.newshubapi.model.dtos.NewsTimeLineDto;
import com.jl.newshubapi.model.dtos.PageResponseResult;
import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.model.entity.NewsItem;
import com.jl.newshubapi.model.entity.Website;
import com.jl.newshubapi.service.ArticleService;
import com.jl.newshubapi.service.IWebsiteService;
import com.jl.newshubapi.utils.RSSUtil;
import com.jl.newshubapi.utils.RequestHandler;
import com.jl.newshubapi.utils.TimeUtil;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.jl.newshubapi.constants.ArticleConstants.ARTICLE_REDIS_KEY_PREFIX;

/**
 * <p>
 * 文章列表 服务实现类
 * </p>
 *
 * @author Jasper
 * @since 2024-08-01
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    IWebsiteService websiteService;

    @Autowired
    private ArticleMapper articleMapper;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // 线程池大小可以根据需要调整


    @Override
    public ResponseResult getArticleList(String source) {
        Website websiteInfo = websiteService.getOne(new QueryWrapper<Website>().eq("id", source));
        if (websiteInfo == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.WEBSITE_NOT_EXIST);
        }
        List<Article> articleList = JSON.parseArray(redisTemplate.opsForValue().get(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId()), Article.class);
        if (articleList != null && articleList.size() > 0) {
            log.debug("从Redis中获取数据");
            HotListVo listVos = HotListVo.builder()
                    .title(websiteInfo.getTitle())
                    .homepage(websiteInfo.getHomepage())
                    .iconUrl(websiteInfo.getIconUrl())
                    .total(articleList.size())
                    .category(websiteInfo.getCategoryName())
                    .updateTime(articleList.get(0).getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .data(articleList)
                    .build();
            return ResponseResult.okResult(listVos);
        } else {
            List<Article> list = list(new QueryWrapper<Article>().eq("source", websiteInfo.getId()).orderBy(true, false, "sort_order").last("limit 50"));
            HotListVo listVos = HotListVo.builder()
                    .title(websiteInfo.getTitle())
                    .homepage(websiteInfo.getHomepage())
                    .iconUrl(websiteInfo.getIconUrl())
                    .total(list.size())
                    .category(websiteInfo.getCategoryName())
                    .updateTime(list.size() == 0 ? TimeUtil.getCurrentUTCTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : list.get(0).getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .data(list)
                    .build();
            //开一个新线程更新Redis,key 为source和category的组合，过期时间为30分钟
            executorService.submit(() -> {
                try {
                    redisTemplate.opsForValue().set(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId() , JSON.toJSONString(list));
                    //过期时间为1小时
                    redisTemplate.expire(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId(), 30, TimeUnit.MINUTES);
                } catch (Exception e) {
                    log.error("更新Redis时出错", e);
                }
            });

            return ResponseResult.okResult(listVos);
        }
    }

    @Override
    public void saveOnlineArticle(Request request, Integer dataSource) {
        String data = RequestHandler.post(request);
        convertAndSave(data,dataSource);
    }

    @Override
    public int saveOnlineArticle(String url, Integer dataSource) {
        String data = RequestHandler.get(url);
        return convertAndSave(data,dataSource);

    }

    @Override
    public void saveOnlineArticle(String url) {
        if (url == null || url.isEmpty()){
            return ;
        }
        SyndFeed feed = RSSUtil.getFeed(url);
        Website website = websiteService.getOne(new QueryWrapper<Website>().eq("fetch_data_url", url));

        RssConverter converter = new RssConverter();
        Article latestArticle = getOne(new QueryWrapper<Article>().eq("source", website.getId()).orderBy(true, false, "updated_time").last("limit 1"));
        List<Article> articleList = converter.convertToArticleList(feed, latestArticle == null ? null : latestArticle.getUpdatedTime());
        if (articleList == null || articleList.isEmpty()) {
            log.info(website.getTitle()+"没有新文章");
            return ;
        }
        //从数据库中获取articleList size大小的数据
        List<Article> list = list(new QueryWrapper<Article>().eq("source", website.getId()).orderBy(true, false, "sort_order").last("limit "+articleList.size()*2));
        //去除重复的文章,用stream
        articleList.removeIf(article -> list.stream().anyMatch(article1 -> article1.getTitle().equals(article.getTitle()) && article1.getLink().equals(article.getLink())));
        int sortOrder = getCurrentMaxOrder(website.getId())+articleList.size()+1;
        for (Article article : articleList) {
            article.setSource(String.valueOf(website.getId()));
            article.setSortOrder(sortOrder--);
        }
        saveOrUpdateBatch(articleList);
        log.info("更新"+website.getTitle()+"文章"+articleList.size()+"篇");
        //删除redis中的数据
        redisTemplate.delete(ARTICLE_REDIS_KEY_PREFIX+website.getId());
//        return articleList.size();
    }




    @Override
    public ResponseResult fetchNewArticles(Integer id) {
        Website website = websiteService.getById(id);
        if (website == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.WEBSITE_NOT_EXIST);
        }

        if (website.getIsRss()) {
            saveOnlineArticle(website.getFetchDataUrl());
        } else {
            saveOnlineArticle(website.getFetchDataUrl(), id);
        }

        return ResponseResult.okResult("更新成功");
    }


    @Transactional(rollbackFor = Exception.class)
    public int convertAndSave(String data, Integer id){
        DataConverter converter = DataConverterFactory.getConverter(id);
        if (converter == null) {
            log.error("Can't find corresponding converter");
            return 0;
        }
        List<Article> articleList = converter.convertToArticleList(data);
        if (articleList == null || articleList.size() == 0) {
            return 0;
        }
        int sortOrder = getCurrentMaxOrder(id);
        //从数据库中获取articleList size大小的数据
        List<Article> list = list(new QueryWrapper<Article>().eq("source", id).orderBy(true, false, "sort_order").last("limit "+articleList.size()*2));
        //去除重复的文章,并设置sortOrder
        articleList.removeIf(article -> list.stream().anyMatch(article1 -> article1.getTitle().equals(article.getTitle()) && article1.getLink().equals(article.getLink())));
        for (Article article : articleList) {
            article.setSource(String.valueOf(id));
            article.setSortOrder(++sortOrder);
        }
        log.info("更新id:"+id+"文章"+articleList.size()+"篇");
        saveOrUpdateBatch(articleList);
        //删除redis中的数据
        redisTemplate.delete(ARTICLE_REDIS_KEY_PREFIX+id);
        return articleList.size();

    }

    //Get news timeline, filter by sources order by updated_time
    @Override
    public ResponseResult getNewsTimeLine(NewsTimeLineDto newsTimeLineDto) {
        String[] sources = newsTimeLineDto.getSources();
        Page<Article> page = new Page<>(newsTimeLineDto.getPage(), newsTimeLineDto.getSize());
        Page<Article> articlePage = articleMapper.selectPage(page, new QueryWrapper<Article>().in("source", sources).orderBy(true, false, "updated_time"));
        if (articlePage.getRecords().size() == 0) {
            return PageResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }else{
            return ResponseResult.okResult(assembleTimeLineData(articlePage.getRecords()));
        }
    }

    // group timeline by update_time and source, put the articles with the same updated_time and from the same source into one NewsListDto
    private List<NewsListDto> assembleTimeLineData(List<Article> articleList){
        //get websiteinfo save source and title into map
        HashMap<String, Website> websiteMap = new HashMap<>();
        List<Website> websiteList = websiteService.list();
        for (Website website : websiteList) {
            websiteMap.put(String.valueOf(website.getId()),website);
        }
        List<NewsListDto> newsListDtos = new ArrayList<>();
        int i = 0;
        while (i < articleList.size()) {
            Article article = articleList.get(i);
            NewsListDto newsListDto = new NewsListDto(article.getId(),article.getSource(),websiteMap.get(article.getSource()).getTitle(),websiteMap.get(article.getSource()).getIconUrl(),article.getUpdatedTime(),null);
            List<NewsItem> list = new ArrayList<>();
            list.add(new NewsItem(article.getId(),article.getTitle(),article.getLink()));
            //遍历剩下的文章，如果有相同的source和update_time则放入list中
            int j = i+1;
            while (j < articleList.size()) {
                Article article1 = articleList.get(j);
                if (article1.getSource().equals(article.getSource()) && article1.getUpdatedTime().equals(article.getUpdatedTime())) {
                    list.add(new NewsItem(article1.getId(),article1.getTitle(),article1.getLink()));
                }else {
                    break;
                }
                j++;
            }
            i = j;
            newsListDto.setNewsList(list);
            i++;
            newsListDtos.add(newsListDto);
        }
        return  newsListDtos;
    }



    private int getCurrentMaxOrder(Integer id){
        Article one = getOne(new QueryWrapper<Article>().select("sort_order").eq("source", id).orderBy(true, false, "sort_order").last("limit 1"));
        return one == null ? 0 : one.getSortOrder();
    }


}
