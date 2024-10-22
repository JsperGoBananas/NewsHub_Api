package com.jl.newshubapi.service;

import com.jl.newshubapi.model.entity.AiSummary;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jl.newshubapi.model.dtos.ResponseResult;
import com.jl.newshubapi.model.entity.Content;
import com.jl.newshubapi.model.entity.ContentData;
import com.jl.newshubapi.model.entity.RequestModel;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jasper
 * @since 2024-10-09
 */
public interface IAiSummaryService extends IService<AiSummary> {

    ResponseResult getSummary(Integer id);

    String getAISummary(Integer source);
}
