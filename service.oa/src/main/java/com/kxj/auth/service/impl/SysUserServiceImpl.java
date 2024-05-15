package com.kxj.auth.service.impl;

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
        if (status ==1){
            user.setStatus(status);
        }else {
            user.setStatus(0);
        }
        this.updateById(user);
    }
}
