package com.jl.newshubapi.converter;

import com.jl.newshubapi.model.entity.Article;

import java.util.List;

public interface DataConverter {
    List<Article> convertToArticleList(String sourceData);

//    HotListVos convertToVo(String source);
}
