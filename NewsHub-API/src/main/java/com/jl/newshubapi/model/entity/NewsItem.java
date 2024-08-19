package com.jl.newshubapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewsItem {
    private Long id;
    private String title;
    private String url;
}
