package com.jl.newshubapi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jasper
 * @since 2024-09-30
 */
@Getter
@Setter
@TableName("reading_list")
public class ReadingList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("article_id")
    private Long articleId;

    @TableField("title")
    private String title;

    @TableField("link")
    private String link;

    @TableField("cover_image")
    private String coverImage;

    @TableField("description")
    private String description;

    @TableField("source")
    private String source;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("is_read")
    private Boolean isRead;

    @TableField("priority")
    private Integer priority;

    @TableField("category")
    private String category;
}
