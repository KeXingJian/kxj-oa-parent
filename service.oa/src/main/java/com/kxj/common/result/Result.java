package com.kxj.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer coed;
    private String message;
    private T data;
    private Result(){

    }
    public static <T> Result<T> ok(){
        return build(null,ResultCodeEnum.SUCCESS);
    }
    public static <T> Result<T> ok(T data){
        return build(data,ResultCodeEnum.SUCCESS);
    }
    public static <T> Result<T> fail(){
        return build(null,ResultCodeEnum.FAIL);
    }
    public static <T> Result<T> fail(T data){
        return build(data,ResultCodeEnum.FAIL);
    }
    public static <T> Result<T> build(T body,ResultCodeEnum resultCodeEnum){
        Result<T> result = new Result<>();
        if (body!=null){
            result.setData(body);
        }
        result.setCoed(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public Result message(String message) {
        this.setMessage(message);
        return this;
    }
    public Result code(Integer code){
        this.setCoed(code);
        return this;
    }
}
