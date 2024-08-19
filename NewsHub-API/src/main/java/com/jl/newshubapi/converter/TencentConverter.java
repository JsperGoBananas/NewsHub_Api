package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class TencentConverter implements DataConverter{
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> articleList = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(sourceData);
        JSONArray hotStories = jsonObject.getJSONArray("idlist").getJSONObject(0).getJSONArray("newslist");
        for (int i = 2; i < hotStories.size(); i++) {
            JSONObject hotStory = hotStories.getJSONObject(i);
            Article article = new Article();
            article.setTitle(hotStory.getString("title"));
            article.setLink(hotStory.getString("url"));
            article.setCoverImage(hotStory.getString("miniProShareImage"));
            if(hotStory.getString("nlpAbstract")!=null){
                article.setDescription(hotStory.getString("nlpAbstract").length()>1000?hotStory.getString("nlpAbstract").substring(0,1000):hotStory.getString("nlpAbstract"));
            }else if (hotStory.getString("abstract")!=null) {
                article.setDescription(hotStory.getString("abstract").length() > 1000 ? hotStory.getString("abstract").substring(0, 1000) : hotStory.getString("abstract"));
            }

            article.setSource(String.valueOf(DataSource.TENCENT_NEWS.getId()));
//            article.setCategory(DataSource.TENCENT_NEWS.getCategory());
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            articleList.add(article);
        }
        return articleList;

    }
}
