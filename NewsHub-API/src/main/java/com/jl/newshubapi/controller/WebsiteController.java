package com.jl.newshubapi.controller;

import com.jl.newshubapi.annotation.RateLimit;
import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.service.IWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 网站表，维护目前所有网站 前端控制器
 * </p>
 *
 * @author Jasper
 * @since 2024-08-04
 */
@RestController
@RequestMapping("/website")
public class WebsiteController {
    @Autowired
    private IWebsiteService websiteService;
    @RateLimit(requests = 100, windowSeconds = 60)
    @GetMapping("/list")
    public ResponseResult list() {
        return websiteService.getWebsiteList();
    }
    @RateLimit(requests = 1, windowSeconds = 60)
    @PostMapping("/add")
    public ResponseResult add(@RequestParam("fetchDataUrl") String fetchDataUrl) {
        return websiteService.addWebsite(fetchDataUrl);
    }
    @RateLimit(requests = 100, windowSeconds = 60)
    @PostMapping("/remove")
    public ResponseResult remove(@RequestParam("id") Integer id) {
        return websiteService.removeWebsite(id);
    }
}
