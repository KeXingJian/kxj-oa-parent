package com.kxj.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kxj.auth.service.SysRoleService;
import com.kxj.common.result.Result;
import com.kxj.model.system.SysRole;
import com.kxj.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
@Api(tags = "SysRole")//localhost:8800/doc.html#/home
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Resource
    private SysRoleService sysRoleService;

    //localhost:8800/admin/system/sysRole/findAll
    @ApiOperation("getAll")
    @GetMapping("findAll")
    public Result<List<SysRole>> findAll(){
        int i=10/0;
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }
    @ApiOperation("page")
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
    @ApiOperation("getRole")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id){
        SysRole role = sysRoleService.getById(id);
        return Result.ok(role);
    }
    @ApiOperation("addRole")
    @PostMapping("save")
    public Result save(@RequestBody @Validated SysRole role){
        sysRoleService.save(role);
        return Result.ok();
    }
    @ApiOperation("updateRole")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){
        sysRoleService.updateById(role);
        return Result.ok();
    }
    @ApiOperation("deleteRole")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        sysRoleService.removeById(id);
        return Result.ok();
    }
    @ApiOperation("deleteRoles")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        sysRoleService.removeByIds(idList);
        return Result.ok();
    }
}
