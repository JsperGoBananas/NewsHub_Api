package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.jl.newshubapi.constants.ArticleConstants.THIRTY_SIX_KR_PREFIX;


public class Kr36Converter implements DataConverter{
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> articles = new ArrayList<>();
        try {
            JSONObject jsonObject = JSON.parseObject(sourceData);
            JSONArray hotRankList = jsonObject.getJSONObject("data").getJSONArray("hotRankList");
            for (int i = 0; i < hotRankList.size(); i++) {
                JSONObject hotData = hotRankList.getJSONObject(i);
                Article article = new Article();
                article.setTitle(hotData.getJSONObject("templateMaterial").getString("widgetTitle"));
                article.setCoverImage(hotData.getJSONObject("templateMaterial").getString("widgetImage"));
                article.setAuthor(hotData.getJSONObject("templateMaterial").getString("authorName"));
//                article.setUpdatedTime(LocalDateTime.parse(hotData.getJSONObject("templateMaterial").getString("publishTime")));
                article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
                article.setCount(hotData.getJSONObject("templateMaterial").getInteger("statRead"));
                article.setLink(THIRTY_SIX_KR_PREFIX+hotData.getString("itemId"));
                articles.add(article);
                article.setSource(String.valueOf(DataSource.THIRTY_SIX_KR.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

}
