package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 *
 * @author Nova
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
