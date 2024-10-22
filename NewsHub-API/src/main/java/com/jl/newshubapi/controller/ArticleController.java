package com.jl.newshubapi.controller;


import com.jl.newshubapi.model.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 文章列表 前端控制器
 * </p>
 *
 * @author Jasper
 * @since 2024-08-01
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    public ResponseResult setFavorite() {
        return null;
    }

}

