package com.kxj.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kxj.common.result.Result;
import com.kxj.process.service.ProcessService;
import com.kxj.vo.process.ProcessQueryVo;
import com.kxj.vo.process.ProcessVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
@RestController
@RequestMapping(value = "/admin/process")
public class ProcessController {
    @Resource
    private ProcessService processService;
    @ApiOperation("获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        ProcessQueryVo processQueryVo){
        Page<ProcessVo> pageParam=new Page<>(page,limit);
        IPage<ProcessVo> pageModel=
                processService.selectPage(pageParam,processQueryVo);
        return Result.ok(pageModel);
    }
}

