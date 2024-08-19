package com.jl.newshubapi.model.ariticle.vos;


import com.jl.newshubapi.model.entity.Article;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HotListVo {
    String title;
    String category;
    String homepage;
    String iconUrl;
    Integer total;
    String updateTime;
    List<Article> data;

}
