package com.nova.backend.enums;

import lombok.Getter;

@Getter
public enum OperationTypeEnum {
    OTHER("其他操作"),
    INSERT("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    SELECT("查询"),
    LOGIN("登录"),
    LOGOUT("登出"),
    EXPORT("导出"),
    IMPORT("导入"),
    UPLOAD("上传"),
    DOWNLOAD("下载");

    private final String description;

    OperationTypeEnum(String description) {
        this.description = description;
    }
}
