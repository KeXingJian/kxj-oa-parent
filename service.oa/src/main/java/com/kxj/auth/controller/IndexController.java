package com.kxj.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kxj.auth.service.SysMenuService;
import com.kxj.auth.service.SysUserService;
import com.kxj.common.config.exception.KxjException;
import com.kxj.common.jwt.JwtHelper;
import com.kxj.common.result.Result;
import com.kxj.model.system.SysUser;
import com.kxj.vo.system.LoginVo;
import com.kxj.vo.system.RouterVo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import com.kxj.common.utils.MD5;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "登入管理")
@RestController()
@RequestMapping("/admin/system/index")
public class IndexController {

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private SysUserService sysUserService;
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        System.out.println("进入数据库校验");
        //通过用户名获取数据库角色
        SysUser sysUser = sysUserService
                .getOne(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername,loginVo.getUsername()));

        //校验
        if (sysUser==null)
            throw new KxjException(201,"user is not exist");
        //登录加密密码与数据库密码比较
        if (!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword()))
            throw new KxjException(201,"password is error");
        if (sysUser.getStatus()==0)
            throw new KxjException(201,"用户已被禁用,请联系管理员");

        //将用户名和id转成token
        String token = JwtHelper
                .createToken(sysUser.getId(), sysUser.getUsername());
        Map<String,Object> map=new HashMap<>();
        map.put("token",token);

        System.out.println(map);
        return Result.ok(map);
    }
    @GetMapping("info")
    public Result info(HttpServletRequest request){

        //通过token获取角色
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        SysUser user = sysUserService.getById(userId);

        //查询数据库动态构建路由结构，进行显示
        List<RouterVo> routerList=sysMenuService.findUserMenuListByUserId(userId);
        //5 根据用户id获取用户可以操作按钮列表57
        List<String> permsList=sysMenuService.findUserPermsByUserId(userId);

        Map<String,Object> map=new HashMap<>();
        map.put("role","[admin]");
        map.put("name",user.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("routers",routerList);
        map.put("buttons",permsList);

        return Result.ok(map);
    }
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
