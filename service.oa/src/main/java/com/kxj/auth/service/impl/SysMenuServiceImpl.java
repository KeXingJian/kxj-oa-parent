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
import com.kxj.vo.system.MetaVo;
import com.kxj.vo.system.RouterVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
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

    //构建所有菜单分支
    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> list = this.list();
        if (CollectionUtils.isEmpty(list)) return null;

        //构建树
        List<SysMenu> result = MenuHelper.buildTree(list);

        return result;
    }

    //查询角色菜单状况
    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {

        //获取有效菜单集
        List<SysMenu> list1 = this.list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1));

        //通过目的角色id,获取角色菜单表
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));

        //抽取目的角色的菜单id
        List<Long> menuIdList = sysRoleMenus
                .stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());

        //与角色集对比,并设置状态
        list1.forEach(sysMenu -> {
            sysMenu.setSelect(menuIdList.contains(sysMenu.getId()));
        });

        //构建树
        List<SysMenu> list2 = MenuHelper.buildTree(list1);
        return list2;
    }

    //分配角色菜单
    @Transactional
    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {

        //清空角色菜单
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId()));

        //通过需添加菜单的idList,重构菜单角色关联表
        for(Long menuId : assginMenuVo.getMenuIdList()){
            if (StringUtils.isEmpty(menuId))continue;
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assginMenuVo.getRoleId());
            rolePermission.setRoleId(menuId);
            sysRoleMenuMapper.insert((rolePermission));
        }
    }

    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList=null;
        if (userId==1){
            //超级管理员,获取所有路由
            sysMenuList=this
                    .list(new LambdaQueryWrapper<SysMenu>()
                            .eq(SysMenu::getStatus,1)
                            .orderByAsc(SysMenu::getSortValue));
        }else {
            //通过u-r表连接r-m表连接m
            sysMenuList=sysMenuMapper.findListByUserId(userId);
        }

        //构建树
        List<SysMenu> sysMenuTreeList=MenuHelper.buildTree(sysMenuList);
        //构建menu
        return this.buildMenus(sysMenuTreeList);
    }

    private List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers=new ArrayList<>();
        menus.forEach(menu->{
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(),menu.getIcon()));
            List<SysMenu> children=menu.getChildren();

            if (menu.getType()==1){
                List<SysMenu> hiddnMenuList = children.stream().filter(item ->
                        !StringUtils.isEmpty(item)
                ).collect(Collectors.toList());
                hiddnMenuList.forEach(hiddenMenu->{
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(),hiddenMenu.getIcon()));
                });
            }else {
                if (!CollectionUtils.isEmpty(children)){
                    if (children.size()>0){
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buildMenus(children));
                }
            }
            routers.add(router);
        });
        return routers;
    }

    public String getPath(SysMenu menu){
        String router="/"+menu.getPath();
        if (menu.getParentId()!=0) router=menu.getPath();
        return router;
    }

    @Override
    public List<String> findUserPermsByUserId(Long userId) {

        List<SysMenu> sysMenuList=null;
        if (userId==1){
            //超级管理员
            sysMenuList=this.list(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus,1));
        }else {
            sysMenuList=sysMenuMapper.findListByUserId(userId);
        }

        return sysMenuList
                .stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
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
