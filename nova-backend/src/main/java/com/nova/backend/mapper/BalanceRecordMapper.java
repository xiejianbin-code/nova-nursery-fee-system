package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.BalanceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 余额变动记录Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface BalanceRecordMapper extends BaseMapper<BalanceRecord> {

}
