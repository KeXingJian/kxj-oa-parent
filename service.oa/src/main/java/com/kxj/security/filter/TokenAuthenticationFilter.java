package com.kxj.security.filter;

import com.alibaba.fastjson.JSON;
import com.kxj.common.jwt.JwtHelper;
import com.kxj.common.result.Result;
import com.kxj.common.result.ResultCodeEnum;
import com.kxj.common.utils.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.SaslServer;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;
    public TokenAuthenticationFilter(RedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (!StringUtils.isEmpty(token)){
            String username = JwtHelper.getUsername(token);
            if (!StringUtils.isEmpty(username)){
                String authString =(String) redisTemplate.opsForValue().get(username);
                if (!StringUtils.isEmpty(authString)){
                    List<Map> maps = JSON.parseArray(authString, Map.class);
                    List<SimpleGrantedAuthority> list=new ArrayList<>();
                    maps.forEach(map->{
                        list.add(new SimpleGrantedAuthority((String)map.get("authority")));
                    });
                    return new UsernamePasswordAuthenticationToken(username,null, list);
                }else {
                    return new UsernamePasswordAuthenticationToken(username,null, new ArrayList<>());

                }

            }
        }
        return null;
    }
}
