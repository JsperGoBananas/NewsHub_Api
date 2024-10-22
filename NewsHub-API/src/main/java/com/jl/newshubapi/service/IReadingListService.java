package com.jl.newshubapi.service;

import com.jl.newshubapi.model.entity.ReadingList;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jasper
 * @since 2024-09-30
 */
public interface IReadingListService extends IService<ReadingList> {

     void addReadingList(ReadingList readingList);

}
