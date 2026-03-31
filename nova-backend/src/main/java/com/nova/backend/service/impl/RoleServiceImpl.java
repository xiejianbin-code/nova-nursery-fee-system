package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.backend.entity.Role;
import com.nova.backend.entity.RoleMenu;
import com.nova.backend.entity.UserRole;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.RoleMapper;
import com.nova.backend.mapper.RoleMenuMapper;
import com.nova.backend.mapper.UserRoleMapper;
import com.nova.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 *
 * @author Nova
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    private static final List<String> FIXED_ROLES = List.of("ADMIN", "FINANCE", "TEACHER");

    @Override
    public Object pageList(Integer page, Integer limit, String name, Integer status) {
        Page<Role> pageInfo = new Page<>(page, limit);
        
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(Role::getRoleName, name);
        }
        if (status != null) {
            queryWrapper.eq(Role::getStatus, status);
        }
        
        queryWrapper.orderByAsc(Role::getSort);
        
        return this.page(pageInfo, queryWrapper);
    }

    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, 1);
        queryWrapper.orderByAsc(Role::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public Object getRoleDetail(Long id) {
        Role role = this.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(Role role) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleCode, role.getRoleCode());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }
        
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, role.getRoleName());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        
        role.setStatus(1);
        this.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role) {
        Role existRole = this.getById(role.getId());
        if (existRole == null) {
            throw new BusinessException("角色不存在");
        }
        
        if (FIXED_ROLES.contains(existRole.getRoleCode())) {
            throw new BusinessException("系统固定角色不允许修改");
        }
        
        if (!existRole.getRoleCode().equals(role.getRoleCode())) {
            LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Role::getRoleCode, role.getRoleCode());
            queryWrapper.ne(Role::getId, role.getId());
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }
        
        if (!existRole.getRoleName().equals(role.getRoleName())) {
            LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Role::getRoleName, role.getRoleName());
            queryWrapper.ne(Role::getId, role.getId());
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException("角色名称已存在");
            }
        }
        
        this.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        Role role = this.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        if (FIXED_ROLES.contains(role.getRoleCode())) {
            throw new BusinessException("系统固定角色不允许删除");
        }
        
        Long userCount = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id)
        );
        if (userCount > 0) {
            throw new BusinessException("该角色已分配给用户，不能删除");
        }
        
        roleMenuMapper.delete(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, id)
        );
        
        this.removeById(id);
    }

    @Override
    public List<Long> getRoleMenus(Long roleId) {
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId)
        );
        
        return roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setRoleMenus(Long roleId, List<Long> menuIds) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        roleMenuMapper.delete(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId)
        );
        
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }
}