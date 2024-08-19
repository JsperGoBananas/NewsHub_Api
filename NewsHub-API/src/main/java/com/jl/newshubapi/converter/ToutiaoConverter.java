package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ToutiaoConverter implements DataConverter{

    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject hotStory = data.getJSONObject(i);
            Article article = new Article();
            article.setTitle(hotStory.getString("Title"));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(0);
            article.setCoverImage(hotStory.getJSONObject("Image").getString("url"));
            String text = hotStory.getString("Url");
            article.setLink(text.substring(0, text.indexOf('?') == -1?text.length():text.indexOf('?')));
            article.setSource(String.valueOf(DataSource.TOUTIAO_NEWS.getId()));
//            article.setCategory(DataSource.TOUTIAO_NEWS.getCategory());
            list.add(article);
        }

        return list;

    }
}
