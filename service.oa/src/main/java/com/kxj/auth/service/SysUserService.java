package com.kxj.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kxj.model.system.SysRole;
import com.kxj.model.system.SysUser;

public interface SysUserService extends IService<SysUser> {
    void updateStatus(Long id, Integer status);

    SysUser getUserByUsername(String username);
}
