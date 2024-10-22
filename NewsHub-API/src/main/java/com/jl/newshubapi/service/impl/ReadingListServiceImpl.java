package com.jl.newshubapi.service.impl;

import com.jl.newshubapi.model.entity.ReadingList;
import com.jl.newshubapi.mapper.ReadingListMapper;
import com.jl.newshubapi.service.IReadingListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jasper
 * @since 2024-09-30
 */
@Service
public class ReadingListServiceImpl extends ServiceImpl<ReadingListMapper, ReadingList> implements IReadingListService {

    @Override
    public void addReadingList(ReadingList readingList) {
        this.save(readingList);
    }
}
