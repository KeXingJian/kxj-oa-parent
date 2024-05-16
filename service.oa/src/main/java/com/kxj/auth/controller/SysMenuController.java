package com.kxj.auth.controller;


import com.kxj.auth.service.SysMenuService;
import com.kxj.common.result.Result;
import com.kxj.model.system.SysMenu;
import com.kxj.vo.system.AssginMenuVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author kxj
 * @since 2024-05-13
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Resource
    private SysMenuService sysMenuService;
    @PreAuthorize("hasAuthority('bnt.sysMenu.list')")
    @ApiOperation("获取菜单")
    @GetMapping("findNodes")
    public Result findNodes(){
        List<SysMenu> list=sysMenuService.findNodes();
        return Result.ok(list);
    }

    @ApiOperation("查询所有菜单和角色分配的菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId){
        List<SysMenu> list=sysMenuService.findSysMenuByRoleId(roleId);
        return Result .ok(list);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.assignAuth')")
    @ApiOperation("角色分配菜单")
    @PostMapping("doAssign")
    public Result doAssign(@RequestBody AssginMenuVo assginMenuVo){
        sysMenuService.doAssign(assginMenuVo);
        return Result.ok();
    }

    //====================================================================
    //=============================以下为CRUD==============================
    //====================================================================
    @PreAuthorize("hasAuthority('bnt.sysMenu.add')")
    @ApiOperation(("添加菜单"))
    @PostMapping("save")
    public Result save(@RequestBody SysMenu permission){
        sysMenuService.save(permission);
        return Result.ok();
    }
    @PreAuthorize("hasAuthority('bnt.sysMenu.update')")
    @ApiOperation("更新菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody SysMenu permission){
        sysMenuService.updateById(permission);
        return Result.ok();
    }
    @PreAuthorize("hasAuthority('bnt.sysMenu.remove')")
    @ApiOperation("删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        sysMenuService.removeById(id);
        return Result.ok();
    }

}

