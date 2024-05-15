package com.kxj.common.jwt;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {
    private static  long TOKEN_EXPIRATION= 365L *24*60*60*1000;
    private static String tokenSignKey="123456";
    public static String createToken(Long userId,String username){
        return Jwts.builder()
                .setSubject("AUTH-USER")
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .claim("userId", userId)
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }
    public static Long getUserId(String token){
        try{
            if(StringUtils.isEmpty(token)) return null;
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            Integer userId =(Integer) body.get("userId");
             return userId.longValue();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String getUsername(String token){
        try{
            if(StringUtils.isEmpty(token)) return "";
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(tokenSignKey)
                    .parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            return (String) body.get("username");
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L, "admin");
        System.out.println(token);
        System.out.println(JwtHelper.getUserId(token));
        System.out.println(JwtHelper.getUsername(token));
    }
}
