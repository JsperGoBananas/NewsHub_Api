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
 * @since 2024-10-09
 */
@Getter
@Setter
@TableName("ai_summary")
public class AiSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("source")
    private Integer source;

    /**
     * summary_content
     */
    @TableField("summary_content")
    private String summaryContent;

    @TableField("generated_time")
    private LocalDateTime generatedTime;
}
