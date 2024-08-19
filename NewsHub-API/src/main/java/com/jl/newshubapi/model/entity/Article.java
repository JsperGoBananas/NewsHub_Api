package com.jl.newshubapi.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文章列表
 * </p>
 *
 * @author Jasper
 * @since 2024-08-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Article对象", description="文章列表")
public class Article implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
//    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "文章链接")
    private String link;

    @ApiModelProperty(value = "封面图片")
    private String coverImage;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "数据")
    private Integer count;

    private String author;
    @ApiModelProperty(value = "来源")
    private String source;

    private LocalDateTime updatedTime;

    @ApiModelProperty("排序")
    private Integer sortOrder;


}
