package com.jl.newshubapi.controller;

import com.jl.newshubapi.context.UserContext;
import com.jl.newshubapi.model.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jasper
 * @since 2024-09-30
 */
@RestController
@RequestMapping("/readingList")
public class ReadingListController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @GetMapping("/list")
    public ResponseResult getReadingList() {
        return ResponseResult.okResult(UserContext.getUser());
    }

    @GetMapping("/send")
    public String sendMessage(@RequestParam("msg") String message) {
        kafkaTemplate.send("test", message);
        return "消息已发送";
    }

}
