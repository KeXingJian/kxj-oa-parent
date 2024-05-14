package com.kxj.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kxj.auth.mapper.SysMenuMapper;
import com.kxj.auth.mapper.SysRoleMapper;
import com.kxj.auth.mapper.SysRoleMenuMapper;
import com.kxj.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kxj.common.config.exception.KxjException;
import com.kxj.model.system.SysMenu;
import com.kxj.model.system.SysRoleMenu;
import com.kxj.system.helper.MenuHelper;
import com.kxj.vo.system.AssginMenuVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author kxj
 * @since 2024-05-13
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> list = this.list();
        if (CollectionUtils.isEmpty(list)) return null;
        List<SysMenu> result = MenuHelper.buildTree(list);
        return result;
    }

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        List<SysMenu> list1 = this.list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1));
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        List<Long> menuIdList = sysRoleMenus
                .stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
        list1.forEach(sysMenu -> {
            sysMenu.setSelect(menuIdList.contains(sysMenu.getId()));
        });
        List<SysMenu> list2 = MenuHelper.buildTree(list1);
        return list2;
    }
    @Transactional
    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId()));
        for(Long menuId : assginMenuVo.getMenuIdList()){
            if (StringUtils.isEmpty(menuId))continue;
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assginMenuVo.getRoleId());
            rolePermission.setRoleId(menuId);
            sysRoleMenuMapper.insert((rolePermission));
        }
    }
    @Override
    public boolean removeById(Serializable id) {
        int count = this.count(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, id));
        if (count>0){
            throw new KxjException(201,"菜单不能删除");
        }
        sysMenuMapper.deleteById(id);
        return false;
    }
}
