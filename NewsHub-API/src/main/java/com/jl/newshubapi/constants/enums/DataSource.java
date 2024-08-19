package com.jl.newshubapi.constants.enums;

public enum DataSource {
    CCTV_NEWS(1, "正在发生"),
    ZHIHU_DAILY(2, "每日发生"),

    GITHUB(3, "Trending"),
    NY_TIMES(4, "World"),
    SSPAI(5, "热门文章"),

    ZHIHU(6, "热榜"),
    BBC_NEWS(7, "Top Stories"),

    WIKIPEDIA_MOST_READ_EN(8, "Most Read"),
    WIKIPEDIA_HISTORY_EN(9, "On this day"),

    WIKIPEDIA_MOST_READ_ZH(10, "热门文章"),
    THIRTY_SIX_KR(11, "热门文章"),

    CTO51(12,  "热门文章"),

    TENCENT_NEWS(13,  "热门文章"),

    TOUTIAO_NEWS(14, "头条热榜")


    ;


    private final Integer id;

    private final String categoryName;

    DataSource(Integer sourceName, String categoryName) {
        this.id = sourceName;
        this.categoryName = categoryName;
    }

    public Integer getId() {
        return id;
    }


    public String getCategoryName() {
        return categoryName;
    }
}
