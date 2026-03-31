package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收费记录Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

}
