package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.ContractChangeLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同变更记录Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface ContractChangeLogMapper extends BaseMapper<ContractChangeLog> {

}
