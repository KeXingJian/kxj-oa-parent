package com.kxj.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kxj.auth.mapper.SysRoleMapper;
import com.kxj.auth.mapper.SysUserMapper;
import com.kxj.auth.service.SysRoleService;
import com.kxj.auth.service.SysUserService;
import com.kxj.model.system.SysRole;
import com.kxj.model.system.SysUser;
import com.kxj.security.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    //更新状态
    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        SysUser user = this.getById(id);

        user.setStatus(status);

        this.updateById(user);
    }

    @Override
    public SysUser getUserByUsername(String username) {

        return this
                .getOne(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
    }

    @Override
    public Map<String, Object> getCurrentUser() {
        SysUser sysUser = baseMapper.selectById(LoginUserInfoHelper.getUserId());
        Map<String,Object> map=new HashMap<>();
        map.put("name",sysUser.getName());
        map.put("phone",sysUser.getPhone());
        return map;
    }
}
