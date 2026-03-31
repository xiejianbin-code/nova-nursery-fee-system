package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.backend.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author Nova
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     *
     * @param page 页码
     * @param limit 每页数量
     * @param name 角色名称
     * @param status 状态
     * @return 分页结果
     */
    Object pageList(Integer page, Integer limit, String name, Integer status);

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<Role> getAllRoles();

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    Object getRoleDetail(Long id);

    /**
     * 新增角色
     *
     * @param role 角色信息
     */
    void addRole(Role role);

    /**
     * 修改角色
     *
     * @param role 角色信息
     */
    void updateRole(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 获取角色菜单权限
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenus(Long roleId);

    /**
     * 设置角色菜单权限
     *
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void setRoleMenus(Long roleId, List<Long> menuIds);
}