package com.kxj.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kxj.auth.mapper.SysRoleMapper;
import com.kxj.auth.mapper.SysUserRoleMapper;
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
    private SysUserRoleMapper sysUserRoleMapper;
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        List<SysRole> allRoleList = this.list();
        List<SysUserRole> existUserRoleList = sysUserRoleMapper
                .selectList(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
                        .select(SysUserRole::getRoleId));
        List<Long> existRoleIdList = existUserRoleList.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        List<SysRole> assginRoelList=new ArrayList<>();
        allRoleList.forEach(role->{
            if (existRoleIdList.contains(role.getId())) {
                assginRoelList.add(role);
            }
        });
        Map<String,Object> roleMap=new HashMap<>();
        roleMap.put("assignRoleList",assginRoelList);
        roleMap.put("allRolesList",allRoleList);
        return roleMap;
    }

    @Override
    @Transactional
    public void doAssign(AssginRoleVo assginRoleVo) {
        sysUserRoleMapper
                .delete(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId,
                                assginRoleVo.getUserId()));
        for (Long roleId : assginRoleVo
                .getRoleIdList()) {
            if (StringUtils.isEmpty(roleId)) continue;
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }

    }
}
