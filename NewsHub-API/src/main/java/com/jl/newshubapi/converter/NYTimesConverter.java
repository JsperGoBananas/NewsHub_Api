package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class NYTimesConverter implements DataConverter{
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> articles = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray list = jsonObject.getJSONArray("results");
        for (int i = 0; i < list.size(); i++) {
            JSONObject hotStory = list.getJSONObject(i);
            Article article = new Article();
            article.setTitle(hotStory.getString("title"));
            article.setDescription(hotStory.getString("abstract").length()>1000?hotStory.getString("abstract").substring(0,1000):hotStory.getString("abstract"));
            if(hotStory.getJSONArray("multimedia")!=null){
                article.setCoverImage(hotStory.getJSONArray("multimedia").getJSONObject(0).getString("url"));
            }

            article.setAuthor(hotStory.getString("byline"));
//            article.setUpdatedTime(LocalDateTime.parse(hotStory.getString("published_date")));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(0);
            article.setLink(hotStory.getString("url"));
            article.setSource(String.valueOf(DataSource.NY_TIMES.getId()));
            articles.add(article);
        }
        return articles;
    }


}
