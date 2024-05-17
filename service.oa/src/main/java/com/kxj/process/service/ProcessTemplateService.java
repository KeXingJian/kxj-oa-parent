package com.kxj.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kxj.model.process.ProcessTemplate;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
public interface ProcessTemplateService extends IService<ProcessTemplate> {
    IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam);

    void publish(Long id);
}
