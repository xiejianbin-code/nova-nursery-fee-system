package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.Alert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警记录Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface AlertMapper extends BaseMapper<Alert> {

}
