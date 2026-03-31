package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.FeeTemplateItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收费标准模板明细Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface FeeTemplateItemMapper extends BaseMapper<FeeTemplateItem> {

}
