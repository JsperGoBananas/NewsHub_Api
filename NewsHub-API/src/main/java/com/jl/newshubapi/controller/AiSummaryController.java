package com.jl.newshubapi.controller;

import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.service.IAiSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jasper
 * @since 2024-10-09
 */
@RestController
@RequestMapping("/aiSummary")
public class AiSummaryController {
    @Autowired
    IAiSummaryService aiSummaryService;

    @GetMapping("/getSummary/{id}")
    public ResponseResult getSummary(@PathVariable("id") Integer id) {
        return aiSummaryService.getSummary(id);
    }

    @GetMapping("test")
    public String test() {
        return "test";
    }

    @PostMapping("/getAISummary")
    public String getAISummary(@RequestParam("url") String url) {
        return aiSummaryService.getAISummary(aiSummaryService.buildContent(url));
    }
}
