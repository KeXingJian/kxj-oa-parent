package com.kxj.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kxj.model.process.Process;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kxj.vo.process.ApprovalVo;
import com.kxj.vo.process.ProcessFormVo;
import com.kxj.vo.process.ProcessQueryVo;
import com.kxj.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
public interface ProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);

    void deployByZip(String deployPath);

    void startUp(ProcessFormVo processFormVo);

    IPage<ProcessVo> findPending(Page<Process> pageParam);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo);

    IPage<ProcessVo> findProcessed(Page<java.lang.Process> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
