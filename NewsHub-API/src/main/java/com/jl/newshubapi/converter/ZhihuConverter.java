package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.jl.newshubapi.constants.ArticleConstants.ZHIHU_PREFIX;

public class ZhihuConverter implements DataConverter{
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> list = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject hotStory = data.getJSONObject(i).getJSONObject("target");
            Article article = new Article();
            article.setTitle(hotStory.getString("title"));
            article.setDescription(hotStory.getString("excerpt").length()>1000?hotStory.getString("excerpt").substring(0,1000):hotStory.getString("excerpt"));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setCount(0);
            article.setLink(ZHIHU_PREFIX + hotStory.getString("id"));
            article.setSource(String.valueOf(DataSource.ZHIHU.getId()));
            list.add(article);
        }

        return list;

    }


}