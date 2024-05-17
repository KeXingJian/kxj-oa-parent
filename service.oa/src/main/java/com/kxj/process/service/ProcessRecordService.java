package com.kxj.process.service;

import com.kxj.model.process.ProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
public interface ProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId,Integer status,String description);
}
