package com.jl.newshubapi.model.dtos;

import lombok.Data;

@Data
public class NewsTimeLineDto  extends  PageRequestDto{
    private String[] sources;
}
