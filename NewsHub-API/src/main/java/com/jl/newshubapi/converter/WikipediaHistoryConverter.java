package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class WikipediaHistoryConverter implements DataConverter {
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray onthisday = jsonObject.getJSONArray("onthisday");
        for (int i = 0; i < onthisday.size(); i++) {
            JSONObject hotStory = onthisday.getJSONObject(i);
            JSONObject firstItem = hotStory.getJSONArray("pages").getJSONObject(0);
            Article article = new Article();
            article.setTitle(hotStory.getString("text"));
            if(firstItem.getJSONObject("originalimage")!=null){
                article.setCoverImage(firstItem.getJSONObject("originalimage").getString("source"));
            }
            article.setDescription(firstItem.getString("extract").length()>1000?firstItem.getString("extract").substring(0,1000):firstItem.getString("extract"));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(firstItem.getInteger("views"));
            article.setLink(firstItem.getJSONObject("content_urls").getJSONObject("desktop").getString("page"));
            article.setSource(String.valueOf(DataSource.WIKIPEDIA_HISTORY_EN.getId()));
            list.add(article);
        }

        return list;

    }


}
