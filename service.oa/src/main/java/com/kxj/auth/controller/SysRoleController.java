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
@Api(tags = "角色管理")//localhost:8800/doc.html#/home
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Resource
    private SysRoleService service;

    //localhost:8800/admin/system/sysRole/findAll
    @ApiOperation("查询所有角色")
    @GetMapping("findAll")
    public Result<List<SysRole>> findAll(){
        List<SysRole> list = service.list();
        return Result.ok(list);
    }
    @ApiOperation("分页条件查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        Page<SysRole> pageParam = new Page<>(page, limit);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        wrapper.like(!StringUtils.isEmpty(roleName),SysRole::getRoleName,roleName);
        Page<SysRole> pageModel = service.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }
    @ApiOperation("获取角色")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id){
        SysRole role = service.getById(id);
        return Result.ok(role);
    }
    @ApiOperation("新增角色")
    @PostMapping("save")
    public Result save(@RequestBody @Validated SysRole role){
        service.save(role);
        return Result.ok();
    }
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){
        service.updateById(role);
        return Result.ok();
    }
    @ApiOperation("删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        service.removeById(id);
        return Result.ok();
    }
    @ApiOperation("根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        service.removeByIds(idList);
        return Result.ok();
    }
}
