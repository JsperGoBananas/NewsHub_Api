package com.jl.newshubapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jl.newshubapi.model.requests.Request;
import com.jl.newshubapi.model.dtos.NewsTimeLineDto;
import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.model.entity.Article;

import java.util.List;

/**
 * <p>
 * 文章列表 服务类
 * </p>
 *
 * @author Jasper
 * @since 2024-08-01
 */
public interface ArticleService extends IService<Article> {
//    ResponseResult getArticleList(DataSource source, WebsiteInfoEnum websiteInfoEnum);


    ResponseResult getArticleList(String source,int page,int size);

    int saveOnlineArticle(String url, Integer datasourceId);

    void saveOnlineArticle(Request request, Integer dataSource);
    //For RSS

    int  saveOnlineArticle(String url);

    ResponseResult getNewsTimeLine(NewsTimeLineDto newsTimeLineDto);

    ResponseResult fetchNewArticles(Integer id);

    List<Article> getLast12HoursArticles(Integer id);
}
