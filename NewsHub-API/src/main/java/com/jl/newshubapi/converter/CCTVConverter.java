package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class CCTVConverter implements DataConverter{
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        sourceData = sourceData.replaceAll("^news\\(|\\);?$", "");
        List<Article> articleList = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray hotStories = jsonObject.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < hotStories.size(); i++) {
            JSONObject hotStory = hotStories.getJSONObject(i);
            Article article = new Article();
            article.setDescription(hotStory.getString("brief").length()>1000?hotStory.getString("brief").substring(0,1000):hotStory.getString("brief"));
            article.setTitle(hotStory.getString("title"));
            article.setCoverImage(hotStory.getString("image"));
//            article.setUpdatedTime(LocalDateTime.parse(hotStory.getString("focus_date")));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime() );
            article.setCount(0);
            article.setLink(hotStory.getString("url"));
            article.setSource(String.valueOf(DataSource.CCTV_NEWS.getId()));
            articleList.add(article);
        }
        return articleList;

    }


}
