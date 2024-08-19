package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ZhihuDailyConverter implements DataConverter{

    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray hotStories = jsonObject.getJSONArray("stories");
        JSONArray hotTopStories = jsonObject.getJSONArray("top_stories");
        for (int i = 0; i < hotStories.size(); i++) {
            JSONObject hotStory = hotStories.getJSONObject(i);
            Article article = new Article();
            article.setTitle(hotStory.getString("title"));
            article.setCoverImage(hotStory.getJSONArray("images").getString(0));
//            article.setUpdatedTime(LocalDateTime.parse(hotStory.getString("ga_prefix")));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(0);
            article.setSource(String.valueOf(DataSource.ZHIHU_DAILY.getId()));
            article.setLink(hotStory.getString("url"));
            list.add(article);
        }
        for (int i = 0; i < hotTopStories.size(); i++) {
            JSONObject hotTopStory = hotTopStories.getJSONObject(i);
            Article article = new Article();
            if (list.stream().anyMatch(item -> item.getTitle().equals(hotTopStory.getString("title")))) {
                continue;
            }
            article.setTitle(hotTopStory.getString("title"));
            article.setCoverImage(hotTopStory.getString("image"));
            article.setAuthor(hotTopStory.getString("hint"));
//            article.setUpdatedTime(hotTopStory.getString("ga_prefix"));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(0);
            article.setLink(hotTopStory.getString("url"));
            article.setSource(String.valueOf(DataSource.ZHIHU_DAILY.getId()));
            list.add(article);
        }
        return list;

    }


}
