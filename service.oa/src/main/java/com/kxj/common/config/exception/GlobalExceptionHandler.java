package com.kxj.common.config.exception;

import com.kxj.common.result.Result;
import com.kxj.common.result.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(ArithmeticException.class)
    public Result error(ArithmeticException arithmeticException){
        arithmeticException.printStackTrace();
        return Result.fail().message("执行了特定处理方法");
    }
    @ExceptionHandler(KxjException.class)
    public Result error(KxjException exception){
        exception.printStackTrace();
        return Result.fail().message(exception.getMessage()).code(exception.getCode());
    }
    @ExceptionHandler(AccessDeniedException.class)
    public Result error(AccessDeniedException e) throws AccessDeniedException {
        return Result.build(null, ResultCodeEnum.PERMISSION);
    }
}
