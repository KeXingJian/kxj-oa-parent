package com.kxj.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.kxj.model.system.SysMenu;
import com.kxj.vo.system.AssginMenuVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author kxj
 * @since 2024-05-13
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assginMenuVo);
}
