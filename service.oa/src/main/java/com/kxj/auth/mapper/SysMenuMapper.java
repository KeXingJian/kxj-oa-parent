package com.kxj.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kxj.model.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author kxj
 * @since 2024-05-13
 */

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findListByUserId(@Param("userId") Long userId);
}
