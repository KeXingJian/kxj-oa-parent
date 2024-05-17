package com.kxj.process.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.kxj.model.process.ProcessType;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
public interface ProcessTypeService extends IService<ProcessType> {

    List<ProcessType> findProcessType();

}
