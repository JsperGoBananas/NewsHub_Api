package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.jl.newshubapi.constants.ArticleConstants.SSPAI_ARTICLE_PREFIX;
import static com.jl.newshubapi.constants.ArticleConstants.SSPAI_IMG_PREFIX;


public class SSPaiConverter implements DataConverter{
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> articles = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray list = jsonObject.getJSONArray("data");
        for (int i = 0; i < list.size(); i++) {
            JSONObject hotStory = list.getJSONObject(i);
            Article article = new Article();
            article.setDescription(hotStory.getString("summary"));
            article.setTitle(hotStory.getString("title"));
            article.setCoverImage(SSPAI_IMG_PREFIX+hotStory.getString("banner"));
            article.setAuthor(hotStory.getString("bbc"));
//            article.setUpdatedTime(LocalDateTime.parse(hotStory.getString("published_date")));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(hotStory.getInteger("like_count"));
            article.setLink(SSPAI_ARTICLE_PREFIX +hotStory.getString("id"));
            article.setSource(String.valueOf(DataSource.SSPAI.getId())) ;
            articles.add(article);
        }
        return articles;
    }


}
