package com.kxj.common.config.exception;

import com.kxj.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class KxjException extends RuntimeException{
    private Integer code;
    private String message;
    public KxjException(Integer code,String message){
        super(message);
        this.code=code;
        this.message=message;
    }
    public KxjException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code= resultCodeEnum.getCode();
        this.message=resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "KxjException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
