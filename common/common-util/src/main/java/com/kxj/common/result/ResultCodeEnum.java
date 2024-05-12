package com.kxj.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"success"),
    FAIL(201,"fail"),
    DATA_ERROR(204,"data-error"),
    PERMISSION(209,"not permission");
    private final Integer code;
    private final String message;
    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
