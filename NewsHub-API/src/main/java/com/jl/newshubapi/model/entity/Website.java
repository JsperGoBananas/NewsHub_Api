package com.jl.newshubapi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("website")
public class Website implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("source")
    private String source;

    @TableField("title")
    private String title;

    @TableField("category_name")
    private String categoryName;

    @TableField("homepage")
    private String homepage;

    @TableField("is_show")
    private Boolean isShow;

    /**
     * 获取数据连接
     */
    @TableField("fetch_data_url")
    private String fetchDataUrl;

    /**
     * 图标地址
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 是否rss订阅
     */
    @TableField("is_rss")
    private Boolean isRss;
}
