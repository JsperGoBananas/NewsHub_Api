package com.jl.newshubapi.model.dtos;

import com.jl.newshubapi.model.entity.NewsItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class NewsListDto {
    private Long id;
    private String source;
    private String title;
    private String iconUrl;
    private LocalDateTime updatedTime;
    private List<NewsItem> newsList;
}
