package com.kxj.process.service.impl;

import com.kxj.auth.service.SysUserService;
import com.kxj.model.process.ProcessRecord;

import com.kxj.model.system.SysUser;
import com.kxj.process.mapper.ProcessRecordMapper;
import com.kxj.process.service.ProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kxj.security.custom.LoginUserInfoHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
@Service
public class ProcessRecordServiceImpl extends ServiceImpl<ProcessRecordMapper, ProcessRecord> implements ProcessRecordService {

    @Resource
    private SysUserService sysUserService;

    @Override
    public void record(Long processId, Integer status, String description) {

        Long userId = LoginUserInfoHelper.getUserId();
        SysUser user = sysUserService.getById(userId);

        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUser(user.getName());
        processRecord.setOperateUserId(user.getId());
        baseMapper.insert(processRecord);
    }
}
