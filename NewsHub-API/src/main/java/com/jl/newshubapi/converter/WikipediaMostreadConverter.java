package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;


public class WikipediaMostreadConverter implements DataConverter{
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONObject mostread = jsonObject.getJSONObject("mostread");
        if (mostread == null) {
            return null;
        }
        JSONArray articles = mostread.getJSONArray("articles");
        for (int i = 0; i < articles.size(); i++) {
            JSONObject hotStory = articles.getJSONObject(i);
            Article article = new Article();
            article.setTitle(hotStory.getString("title"));
            if(hotStory.getJSONObject("originalimage")!=null){
                article.setCoverImage(hotStory.getJSONObject("originalimage").getString("source"));
            }
            article.setDescription(hotStory.getString("extract").length()>1000?hotStory.getString("extract").substring(0,1000):hotStory.getString("extract"));
//            article.setUpdatedTime(LocalDateTime.parse(hotStory.getString("timestamp")));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(hotStory.getInteger("views"));
            article.setLink(hotStory.getJSONObject("content_urls").getJSONObject("desktop").getString("page"));
            article.setSource(String.valueOf(DataSource.WIKIPEDIA_MOST_READ_EN.getId()));
            list.add(article);
        }

        return list;

    }

}
