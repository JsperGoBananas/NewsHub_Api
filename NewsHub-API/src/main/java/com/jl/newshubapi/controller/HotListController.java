package com.jl.newshubapi.controller;


import com.jl.newshubapi.annotation.Ignore;
import com.jl.newshubapi.annotation.RateLimit;
import com.jl.newshubapi.model.dtos.NewsTimeLineDto;
import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotlist")
public class HotListController {
    public ArticleService getArticleService() {
        return articleService;
    }
// Get 36Kr hot list

    @Autowired
    ArticleService articleService;
    @RateLimit(requests = 100, windowSeconds = 60)
    @GetMapping("/{source}")
    public ResponseResult getHotList(@PathVariable("source") String source , @Ignore  @RequestParam( name = "page",defaultValue = "1") int page, @Ignore @RequestParam(name = "size",defaultValue = "10") int size) {
        return articleService.getArticleList(source,page,size);
    }
    @RateLimit(requests = 100, windowSeconds = 60)
    @PostMapping("/timeline")
    public ResponseResult getTimeline(@RequestBody NewsTimeLineDto newsTimeLineDto) {
        return articleService.getNewsTimeLine(newsTimeLineDto);
    }

    @RateLimit(requests = 1, windowSeconds = 60)
    @PostMapping("/update")
    public ResponseResult updateHotList(@RequestParam("id") Integer id) {
        return articleService.fetchNewArticles(id);
    }

}
