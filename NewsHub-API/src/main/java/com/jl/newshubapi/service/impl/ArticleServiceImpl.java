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
import com.jl.newshubapi.utils.RequestUtil;
import com.jl.newshubapi.utils.TimeUtil;
import com.rometools.rome.feed.synd.SyndFeed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jl.newshubapi.constants.ArticleConstants.ARTICLE_REDIS_COUNT_KEY_PREFIX;
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

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;




    @Override
    public ResponseResult getArticleList(String source,int page, int size) {


        Website websiteInfo = websiteService.getOne(new QueryWrapper<Website>().eq("id", source));
        if (websiteInfo == null) {
            redisTemplate.opsForValue().set(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId() , null);
            redisTemplate.expire(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId(), 1, TimeUnit.MINUTES);
            return ResponseResult.errorResult(AppHttpCodeEnum.WEBSITE_NOT_EXIST);
        }
//        List<Article> articleList = JSON.parseArray(redisTemplate.opsForValue().get(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId()), Article.class);
        if (page <= 5 && redisTemplate.hasKey(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId())) {
            List<Article> articleList = redisTemplate.opsForList().range(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId(), (page - 1) * size, page * size - 1).stream().map(s -> JSON.parseObject(s, Article.class)).collect(Collectors.toList());

            log.debug("从Redis中获取数据");
            System.out.println("从Redis中获取数据");
            HotListVo listVos = buildHotListVo(websiteInfo, articleList,redisTemplate.opsForValue().get(ARTICLE_REDIS_COUNT_KEY_PREFIX + websiteInfo.getId()) == null ? 0 : Integer.parseInt(redisTemplate.opsForValue().get(ARTICLE_REDIS_COUNT_KEY_PREFIX + websiteInfo.getId())));
            return ResponseResult.okResult(listVos);


        } else {
            System.out.println("从数据库中获取数据");
            Page<Article> list = page(new Page<>(page, size), new QueryWrapper<Article>().eq("source", source).orderByDesc("updated_time").orderByDesc("id"));
//            List<Article> list = list(new QueryWrapper<Article>().eq("source", websiteInfo.getId()).orderByDesc("updated_time").orderByDesc("id").last("limit 50"));
            HotListVo listVos = buildHotListVo(websiteInfo, list.getRecords(), (int) list.getTotal());
            //开一个新线程更新Redis,key 为source和category的组合，过期时间为30分钟
            threadPoolTaskExecutor.submit(() -> {
                try {
                    if (!redisTemplate.hasKey(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId())) {
                        List<Article> first100Articles = getFirst100Articles(source);
                        redisTemplate.opsForList().rightPushAll(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId(), first100Articles.stream().map(JSON::toJSONString).collect(Collectors.toList()));
                        //过期时间为30分钟
                        redisTemplate.expire(ARTICLE_REDIS_KEY_PREFIX + websiteInfo.getId(), 30, TimeUnit.MINUTES);
                        redisTemplate.opsForValue().set(ARTICLE_REDIS_COUNT_KEY_PREFIX + websiteInfo.getId(), String.valueOf(list.getTotal()));
                    }

                } catch (Exception e) {
                    log.error("更新Redis时出错", e);
                }
            });
            return ResponseResult.okResult(listVos);
        }
    }

    private List<Article> getFirst100Articles(String source) {
        return list(new QueryWrapper<Article>().eq("source", source).orderByDesc("updated_time").last("limit 100"));
    }

    private HotListVo buildHotListVo(Website websiteInfo, List<Article> articleList,int size) {
        return HotListVo.builder()
                .title(websiteInfo.getTitle())
                .homepage(websiteInfo.getHomepage())
                .iconUrl(websiteInfo.getIconUrl())
                .total(size)
                .category(websiteInfo.getCategoryName())
                .updateTime(articleList.get(0).getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .data(articleList)
                .build();
    }

    @Override
    public void saveOnlineArticle(Request request, Integer dataSource) {
        String data = RequestUtil.post(request);
        convertAndSave(data,dataSource);
    }

    @Override
    public int saveOnlineArticle(String url, Integer dataSource) {
        String data = RequestUtil.get(url);
        return convertAndSave(data,dataSource);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveOnlineArticle(String url) {
        if (url == null || url.isEmpty()){
            return 0;
        }
        SyndFeed feed = RSSUtil.getFeed(url);
        Website website = websiteService.getOne(new QueryWrapper<Website>().eq("fetch_data_url", url));

        RssConverter converter = new RssConverter();
        Article latestArticle = getOne(new QueryWrapper<Article>().eq("source", website.getId()).orderByDesc("updated_time").orderByDesc("id").last("limit 1"));
        List<Article> articleList = converter.convertToArticleList(feed, latestArticle == null ? null : latestArticle.getUpdatedTime(), String.valueOf(website.getId()));
        if (articleList == null || articleList.isEmpty()) {
            log.info(website.getTitle()+"没有新文章");
            return 0;
        }
        //从数据库中获取articleList size大小的数据
        List<String> list = listObjs(new QueryWrapper<Article>().select("link").eq("source", website.getId()).orderByDesc( "updated_time").orderByDesc("id").last("limit "+articleList.size()*2));
        //去除重复的文章,用stream
        Set<String> set = new HashSet<>(list);
        articleList = articleList.stream().filter(article -> !set.contains(article.getLink())).collect(Collectors.toList());
        saveOrUpdateBatch(articleList);
        log.info("更新"+website.getTitle()+"文章"+articleList.size()+"篇");
        //删除redis中的数据
        threadPoolTaskExecutor.submit(()->{
            redisTemplate.delete(ARTICLE_REDIS_KEY_PREFIX+website.getId());
        });
        return articleList.size();

    }

//    @KafkaListener(topics = "test" )
//    public void testConsumer(String message) {
//        //TODO
//        System.out.println(message);

//    }


    @Override
    public ResponseResult fetchNewArticles(Integer id) {
        Website website = websiteService.getById(id);
        if (website == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.WEBSITE_NOT_EXIST);
        }
        int count = 0;
        if (website.getIsRss()) {
            count = saveOnlineArticle(website.getFetchDataUrl());
        } else {
            count = saveOnlineArticle(website.getFetchDataUrl(), id);
        }
        log.info("更新"+website.getTitle()+"文章"+count+"篇");
        return ResponseResult.okResult("更新记录"+count+"条");
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
//        int sortOrder = getCurrentMaxOrder(id);
        //从数据库中获取articleList size大小的数据
//        List<Article> list = list(new QueryWrapper<Article>().eq("source", id).orderBy(true, false, "sort_order").last("limit "+articleList.size()*2));
//        //去除重复的文章,并设置sortOrder
//        articleList.removeIf(article -> list.stream().anyMatch(article1 -> article1.getTitle().equals(article.getTitle()) && article1.getLink().equals(article.getLink())));
        List<String> list = listObjs(new QueryWrapper<Article>().select("link").eq("source", id).orderBy(true, false, "updated_time,id").in("link", articleList.stream().map(Article::getLink).collect(Collectors.toList())));
        Set<String> set = new HashSet<>(list);
        //去除重复的文章,用stream
        articleList = articleList.stream().filter(article -> !set.contains(article.getLink())).collect(Collectors.toList());
        for (Article article : articleList) {
            article.setSource(String.valueOf(id));
//            article.setSortOrder(++sortOrder);
        }
        log.info("更新id:"+id+"文章"+articleList.size()+"篇");
        saveOrUpdateBatch(articleList);

        threadPoolTaskExecutor.submit(()->{
            //删除redis中的数据
            redisTemplate.delete(ARTICLE_REDIS_KEY_PREFIX+id);
        });
        return articleList.size();

    }


    /***
     * 获取时间线
     * @param newsTimeLineDto
     * @return ResponseResult
     */
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

//    BigDecimal

    // get the last 12 hours articles from the same source
    @Override
    public List<Article> getLast12HoursArticles(Integer id) {
        return articleMapper.selectList(new QueryWrapper<Article>().eq("source", id).ge("updated_time", TimeUtil.get12HoursAgoTime()));
    }
}
