package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.Refund;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退费记录Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface RefundMapper extends BaseMapper<Refund> {

}
