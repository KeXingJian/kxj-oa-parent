package com.kxj.auth.service.impl;

import com.kxj.auth.service.SysMenuService;
import com.kxj.auth.service.SysUserService;
import com.kxj.model.system.SysUser;
import com.kxj.security.custom.CustomUser;
import com.kxj.security.custom.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser=sysUserService.getUserByUsername(username);
        if (sysUser==null) throw new UsernameNotFoundException("用户名不存在");
        if (sysUser.getStatus()==0) throw new RuntimeException("账户已停用");
        List<String> userPermsList = sysMenuService.findUserPermsByUserId(sysUser.getId());
        List<SimpleGrantedAuthority> authorityList=new ArrayList<>();
        userPermsList.forEach(perm->{
            authorityList.add(new SimpleGrantedAuthority(perm.trim()));
        });
        return new CustomUser(sysUser, authorityList);
    }
}
