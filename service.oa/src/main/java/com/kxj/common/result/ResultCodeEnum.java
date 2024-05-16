package com.kxj.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"success"),
    FAIL(201,"fail"),
    LOGIN_ERROR(204,"认证失败"),
    PERMISSION(209,"not permission");
    private final Integer code;
    private final String message;
    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
