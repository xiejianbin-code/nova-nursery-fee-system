package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.Contract;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface ContractMapper extends BaseMapper<Contract> {

}
