package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单Mapper
 *
 * @author Nova
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
}
