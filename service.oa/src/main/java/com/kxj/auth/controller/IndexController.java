package com.kxj.auth.controller;

import com.kxj.common.result.Result;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "loginIndex")
@RestController("/admin/system/index")
public class IndexController {
    @PostMapping("login")
    public Result login(){
        Map<String,Object> map=new HashMap();
        map.put("token","admin");
        return Result.ok(map);
    }
    @GetMapping("info")
    public Result info(){
        Map<String,Object> map=new HashMap<>();
        map.put("role","[admin]");
        map.put("name","admin");
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        return Result.ok(map);
    }
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}