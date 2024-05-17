package com.kxj.process.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kxj.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kxj.vo.process.ProcessQueryVo;
import com.kxj.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
public interface ProcessMapper extends BaseMapper<Process> {
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam,@Param("vo") ProcessQueryVo processQueryVo);

}

