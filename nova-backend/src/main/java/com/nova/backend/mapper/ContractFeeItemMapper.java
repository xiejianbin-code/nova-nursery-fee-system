package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.ContractFeeItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同年龄段收费明细Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface ContractFeeItemMapper extends BaseMapper<ContractFeeItem> {

}
