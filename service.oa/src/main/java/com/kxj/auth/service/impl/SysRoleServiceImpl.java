package com.kxj.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kxj.auth.mapper.SysRoleMapper;
import com.kxj.auth.mapper.SysUserRoleMapper;
import com.kxj.auth.service.SysUserRoleService;
import com.kxj.model.system.SysRole;
import com.kxj.auth.service.SysRoleService;
import com.kxj.model.system.SysUserRole;
import com.kxj.vo.system.AssginRoleVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Resource
    private SysUserRoleService sysUserRoleService;

    //获取用户拥有的角色
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        //获取角色表
        List<SysRole> allRoleList = this.list();

        //通过目的userId,查询用户角色关联表的角色
        List<SysUserRole> existUserRoleList = sysUserRoleService
                .list(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId));
        System.out.println(allRoleList);
        //将目的user所有角色id抽取
        List<Long> existRoleIdList = existUserRoleList.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        System.out.println(existRoleIdList);
        //与角色表对比,存在Role,则放入List
        List<SysRole> assginRoelList=new ArrayList<>();
        allRoleList.forEach(role->{
            if (existRoleIdList.contains(role.getId())) {
                assginRoelList.add(role);
            }
        });

        Map<String,Object> roleMap=new HashMap<>();
        roleMap.put("assginRoleList",assginRoelList);
        roleMap.put("allRolesList",allRoleList);
        return roleMap;
    }


    //为用户分配角色
    @Override
    @Transactional
    public void doAssign(AssginRoleVo assginRoleVo) {
        //清除原有角色
        sysUserRoleService
                .remove(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId,
                                assginRoleVo.getUserId()));
        //获取需要添加的角色,修改用户角色关联表
        for (Long roleId : assginRoleVo
                .getRoleIdList()) {
            if (StringUtils.isEmpty(roleId)) continue;

            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(roleId);

            sysUserRoleService.save(userRole);
        }

    }
}
