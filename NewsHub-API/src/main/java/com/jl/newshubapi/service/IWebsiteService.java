package com.jl.newshubapi.service;

import com.jl.newshubapi.model.entity.Website;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jl.newshubapi.model.dtos.ResponseResult;

/**
 * <p>
 * 网站表，维护目前所有网站 服务类
 * </p>
 *
 * @author Jasper
 * @since 2024-08-04
 */
public interface IWebsiteService extends IService<Website> {

    ResponseResult getWebsiteList();

    ResponseResult addWebsite(String fetchDataUrl);

    ResponseResult removeWebsite(Integer fetchDataUrl);
}
