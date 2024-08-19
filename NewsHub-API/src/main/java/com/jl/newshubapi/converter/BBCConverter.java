package com.jl.newshubapi.converter;


import com.alibaba.fastjson.JSONArray;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.jl.newshubapi.constants.ArticleConstants.BBC_PREFIX;


public class BBCConverter implements DataConverter{

    @Override
    public List<Article> convertToArticleList(String sourceData) {
        JSONArray jsonArray = JSONArray.parseObject(sourceData).getJSONArray("data");
        List<Article> articleList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Article article = new Article();
            article.setTitle(jsonArray.getJSONObject(i).getString("title"));
            article.setDescription(jsonArray.getJSONObject(i).getString("summary").length()>1000?jsonArray.getJSONObject(i).getString("summary").substring(0,1000):jsonArray.getJSONObject(i).getString("summary"));
            article.setCoverImage(jsonArray.getJSONObject(i).getJSONObject("indexImage").getJSONObject("model").getJSONObject("blocks").getString("src")) ;
            article.setAuthor(jsonArray.getJSONObject(i).getString("bbc"));
            //String Datetime to LocalDatat
//            article.setUpdatedTime(LocalDateTime.parse(jsonArray.getJSONObject(i).getString("lastUpdated")));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(0);
            article.setLink(BBC_PREFIX+jsonArray.getJSONObject(i).getString("path"));
            article.setSource(String.valueOf(DataSource.BBC_NEWS.getId()));
//            article.setCategory(DataSource.BBC_NEWS.getCategory());
            articleList.add(article);
        }
        return articleList;
    }

}
