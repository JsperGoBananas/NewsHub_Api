package com.jl.newshubapi.controller;

import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.service.IAiSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/getSummary")
    public ResponseResult getSummary(@PathVariable("id") Integer id) {
        return aiSummaryService.getSummary(id);
    }


}
