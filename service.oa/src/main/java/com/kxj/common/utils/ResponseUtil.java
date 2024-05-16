package com.kxj.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxj.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {
    public static void out(HttpServletResponse response, Result r){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            objectMapper.writeValue(response.getWriter(),r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
