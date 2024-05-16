package com.kxj.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kxj.auth.mapper.SysRoleMapper;
import com.kxj.auth.mapper.SysUserMapper;
import com.kxj.auth.service.SysRoleService;
import com.kxj.auth.service.SysUserService;
import com.kxj.model.system.SysRole;
import com.kxj.model.system.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
