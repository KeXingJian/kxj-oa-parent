package com.kxj.security.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kxj.common.jwt.JwtHelper;
import com.kxj.common.result.Result;
import com.kxj.common.result.ResultCodeEnum;
import com.kxj.common.utils.ResponseUtil;
import com.kxj.security.custom.CustomUser;
import com.kxj.vo.system.LoginVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private RedisTemplate redisTemplate;

    public TokenLoginFilter(AuthenticationManager authenticationManager, RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.setAuthenticationManager(authenticationManager);
        this.setPostOnly(false);
        //指定登录接口及提交方式，可以指定任意路径
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/system/index/login","POST"));
        System.out.println("配置了指定过滤接口");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginVo loginVo = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginVo.class);
            Authentication authentication=
                    new UsernamePasswordAuthenticationToken(loginVo.getUsername(),loginVo.getPassword());
            System.out.println("进行了校验");
            return this.getAuthenticationManager().authenticate(authentication);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        CustomUser customUser=(CustomUser) authResult.getPrincipal();
        String token = JwtHelper.createToken(customUser
                        .getSysUser()
                        .getId(),
                customUser
                        .getSysUser()
                        .getUsername());

        System.out.println("name: "+customUser.getUsername()+ " Authorities: "+JSON.toJSONString(customUser.getAuthorities()));

        redisTemplate.opsForValue()
                .set(customUser.getUsername(),
                        JSON.toJSONString(customUser.getAuthorities()));

        Map<String,Object> map=new HashMap<>();
        map.put("token",token);
        System.out.println(map);

        ResponseUtil.out(response, Result.ok(map));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        if (failed != null){
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }
}
