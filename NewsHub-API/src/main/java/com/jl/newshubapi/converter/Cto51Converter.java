package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class Cto51Converter implements DataConverter {
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> articles = new ArrayList<>();
        try {
            JSONObject jsonObject = JSON.parseObject(sourceData);
            JSONArray hotRankList = jsonObject.getJSONObject("data").getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < hotRankList.size(); i++) {
                JSONObject hotData = hotRankList.getJSONObject(i);
                Article article = new Article();
                article.setTitle(hotData.getString("title"));
                article.setDescription(hotData.getString("abstract").length()>1000?hotData.getString("abstract").substring(0,1000):hotData.getString("abstract"));
                article.setCoverImage(hotData.getString("cover"));
                article.setAuthor(hotData.getString("author"));
                article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
                article.setCount(0);
                article.setLink(hotData.getString("url"));
                article.setSource(String.valueOf(DataSource.CTO51.getId()));
//                article.setCategory(DataSource.CTO51.getCategory());
                articles.add(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }
}