package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联Mapper
 *
 * @author Nova
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
}
