package com.kxj.process.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kxj.auth.service.SysUserService;
import com.kxj.common.jwt.JwtHelper;
import com.kxj.common.result.Result;
import com.kxj.model.system.SysUser;
import com.kxj.vo.wechat.BindPhoneVo;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;


@Controller
@RequestMapping("/admin/wechat")
@CrossOrigin //跨域
public class WechatController {
    @Resource
    private SysUserService sysUserService;

    @Resource
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;
    @GetMapping("authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl,
                            HttpServletRequest request) {
        String redirectUrl=null;
        redirectUrl=wxMpService.getOAuth2Service()
                .buildAuthorizationUrl(userInfoUrl,
                        WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                        URLEncoder.encode(returnUrl.replace("guiguoa","#")));
        return "redirect:"+redirectUrl;
    }
    @GetMapping("userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws Exception {
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        String openId = accessToken.getOpenId();
        System.out.println("openId:"+openId);

        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken,null);
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getOpenId, openId));
        String token="";

        if (sysUser!=null)
            token= JwtHelper.createToken(sysUser.getId(),sysUser.getUsername());

        if (returnUrl.indexOf("?")==-1){
            return "redirect:"+returnUrl+"?token="+token+"&openId="+openId;
        }else {
            return "redirect:" + returnUrl + "&token=" + token + "&openId=" + openId;
        }


    }
    @PostMapping("bindPhone")
    @ResponseBody
    public Result bindPhone(@RequestBody BindPhoneVo bindPhoneVo) {
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, bindPhoneVo.getPhone()));
        if (sysUser!=null){
            sysUser.setOpenId(bindPhoneVo.getOpenId());
            sysUserService.updateById(sysUser);
            String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
            return Result.ok(token);
        }else {
            return Result.fail("手机号不存在,请联系管理员");
        }
    }
}
