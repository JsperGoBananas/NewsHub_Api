package com.jl.newshubapi.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jl.newshubapi.constants.enums.DataSource;
import com.jl.newshubapi.model.entity.Article;
import com.jl.newshubapi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jl.newshubapi.constants.ArticleConstants.GITHUB_REPO_PREFIX;


public class GithubConverter implements DataConverter {
    @Override
    public List<Article> convertToArticleList(String sourceData) {
        List<Article> articles = new ArrayList<>();
        JSONArray list = JSON.parseArray(sourceData);
        for (int i = 0; i < list.size(); i++) {
            JSONObject hotStory = list.getJSONObject(i);
            Article article = new Article();
            article.setTitle(hotStory.getString("repo").substring(1));
            article.setAuthor(hotStory.getJSONArray("build_by").stream().map(item-> ((JSONObject)item).getString("by").substring(1)).collect(Collectors.joining(",")));
            article.setUpdatedTime(TimeUtil.getCurrentUTCTime());
            article.setDescription(hotStory.getString("desc"));
            article.setCount(0);
            article.setLink(GITHUB_REPO_PREFIX+hotStory.getString("repo"));
            article.setSource(String.valueOf(DataSource.GITHUB.getId()));
            articles.add(article);
        }
        return articles;
    }


}