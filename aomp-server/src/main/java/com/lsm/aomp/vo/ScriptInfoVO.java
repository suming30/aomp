package com.lsm.aomp.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScriptInfoVO {

    private Long id;
    private String scriptName;
    private String scriptType;
    private String scope;
    private String content;
    private String description;
    private Integer version;
    private String paramsSchema;
    private Boolean isDraft;
    private String auditStatus;
    private Integer runCount;
    private Integer successCount;
    private Integer failCount;
    private String createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
