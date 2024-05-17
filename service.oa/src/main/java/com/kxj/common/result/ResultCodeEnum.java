package com.kxj.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"success"),
    FAIL(201,"fail"),
    LOGIN_ERROR(208,"认证失败"),
    PERMISSION(209,"无权限");
    private final Integer code;
    private final String message;
    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
