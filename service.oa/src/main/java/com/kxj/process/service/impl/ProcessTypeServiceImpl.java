package com.kxj.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kxj.model.process.ProcessTemplate;
import com.kxj.model.process.ProcessType;
import com.kxj.process.mapper.ProcessTypeMapper;
import com.kxj.process.service.ProcessTemplateService;
import com.kxj.process.service.ProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.engine.repository.Deployment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
@Service
public class ProcessTypeServiceImpl extends ServiceImpl<ProcessTypeMapper, ProcessType> implements ProcessTypeService {

    @Resource
    private ProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessType() {
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        processTypeList.forEach(processType -> {
            Long typeId = processType.getId();
            List<ProcessTemplate> processTemplateList = processTemplateService.list(new LambdaQueryWrapper<ProcessTemplate>()
                    .eq(ProcessTemplate::getProcessTypeId,typeId));
            processType.setProcessTemplateList(processTemplateList);
        });
        return processTypeList;
    }
}
