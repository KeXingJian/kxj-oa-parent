package com.kxj.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kxj.auth.service.SysRoleService;
import com.kxj.common.result.Result;
import com.kxj.model.system.SysRole;
import com.kxj.vo.system.AssginRoleVo;
import com.kxj.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Resource
    private SysRoleService sysRoleService;

    @ApiOperation("通过用户获取角色")
    @GetMapping("toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId){
        Map<String,Object> roleMap=sysRoleService.findRoleByAdminId(userId);
        return Result.ok(roleMap);
    }

    @ApiOperation("通过用户分配角色")
    @PutMapping("doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo){
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    //====================================================================
    //=============================以下为CRUD==============================
    //====================================================================

    @ApiOperation("获取全部角色")
    @GetMapping("findAll")
    public Result<List<SysRole>> findAll(){
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        Page<SysRole> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        wrapper.like(StringUtils.isEmpty(roleName),SysRole::getRoleName,roleName);
        Page<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }
    @ApiOperation("获取角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        SysRole role = sysRoleService.getById(id);
        return Result.ok(role);
    }
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody @Validated SysRole role){
        sysRoleService.save(role);
        return Result.ok();
    }
    @ApiOperation("更新角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){
        sysRoleService.updateById(role);
        return Result.ok();
    }
    @ApiOperation("删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        sysRoleService.removeById(id);
        return Result.ok();
    }
    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        sysRoleService.removeByIds(idList);
        return Result.ok();
    }
}
