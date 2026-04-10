package com.lsm.aomp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ScriptCreateDTO {

    @NotBlank(message = "脚本名称不能为空")
    @Size(max = 200, message = "脚本名称不超过200字符")
    private String scriptName;

    @NotBlank(message = "脚本类型不能为空")
    private String scriptType;

    private String scope = "private";

    @NotBlank(message = "脚本内容不能为空")
    private String content;

    @Size(max = 1000, message = "描述不超过1000字符")
    private String description;

    private String paramsSchema;

    private Boolean isDraft = false;

    private List<Long> tagIds;
}
