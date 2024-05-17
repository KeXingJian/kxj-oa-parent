package com.kxj.wechat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.kxj.model.wechat.Menu;
import com.kxj.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author kxj
 * @since 2024-05-17
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
