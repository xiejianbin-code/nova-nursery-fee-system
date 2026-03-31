package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.BalanceAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 余额账户Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface BalanceAccountMapper extends BaseMapper<BalanceAccount> {

}
