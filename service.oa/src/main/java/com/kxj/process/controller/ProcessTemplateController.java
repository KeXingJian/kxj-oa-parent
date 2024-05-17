package com.kxj.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kxj.common.result.Result;
import com.kxj.model.process.ProcessTemplate;
import com.kxj.process.service.ProcessTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
@Api(value = "审批模板管理", tags = "审批模板管理")
@RestController
@RequestMapping(value = "/admin/process/processTemplate")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProcessTemplateController {



    @Resource
    private ProcessTemplateService processTemplateService;

    @ApiOperation("发布")
    @GetMapping("publish/{id}")
    public Result publish(@PathVariable Long id){
        processTemplateService.publish(id);
        return Result.ok();
    }

    @ApiOperation(value = "上传流程定义")
    @PostMapping("uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {
        //获取classes目录位置
        String path = new File(ResourceUtils.getURL("classpath:")
                .getPath()).getAbsolutePath();
        //设置上传文件夹
        File tempFile = new File(path + "/processes/");
        if(!tempFile.exists()) {
            tempFile.mkdirs();
        }
        //创建空文件，实现文件写入
        String filename = file.getOriginalFilename();
        File zipFile = new File(path + "/processes/" + filename);

        //保存文件
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail();
        }

        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + filename);
        map.put("processDefinitionKey", filename.substring(0, filename.lastIndexOf(".")));
        return Result.ok(map);

    }


    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessTemplate> pageParam=new Page<>(page,limit);
        IPage<ProcessTemplate> processTemplateIPage = processTemplateService.selectPage(pageParam);
        return Result.ok(processTemplateIPage);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        processTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        processTemplateService.removeById(id);
        return Result.ok();
    }
}

