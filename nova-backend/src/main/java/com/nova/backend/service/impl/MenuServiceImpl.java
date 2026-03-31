package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.backend.dto.UserMenuVO;
import com.nova.backend.entity.Menu;
import com.nova.backend.entity.RoleMenu;
import com.nova.backend.entity.UserRole;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.MenuMapper;
import com.nova.backend.mapper.RoleMapper;
import com.nova.backend.mapper.RoleMenuMapper;
import com.nova.backend.mapper.UserRoleMapper;
import com.nova.backend.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author Nova
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleMapper roleMapper;

    @Override
    public List<UserMenuVO> getUserMenus(Long userId) {
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 检查是否是超级管理员（ADMIN角色）
        boolean isAdmin = roleIds.stream()
                .anyMatch(roleId -> {
                    com.nova.backend.entity.Role role = roleMapper.selectById(roleId);
                    return role != null && "ADMIN".equals(role.getRoleCode());
                });

        List<Menu> menus;
        if (isAdmin) {
            // 超级管理员获取所有启用的菜单
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Menu::getStatus, 1)
                    .ne(Menu::getMenuType, 3)
                    .orderByAsc(Menu::getSort);
            menus = this.list(wrapper);
        } else {
            // 普通用户获取有权限的菜单
            List<Long> menuIds = getRoleMenuIds(roleIds);
            if (menuIds.isEmpty()) {
                return new ArrayList<>();
            }
            menus = getMenusByIds(menuIds);
        }
        
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<UserMenuVO> getMenuTree() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getSort);
        List<Menu> menus = this.list(wrapper);
        
        return buildMenuTree(menus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        Menu menu = this.getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        
        Long childCount = this.count(
                new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("存在子菜单，不能删除");
        }
        
        roleMenuMapper.delete(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, id)
        );
        
        this.removeById(id);
    }

    private List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    private List<Long> getRoleMenuIds(List<Long> roleIds) {
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoleMenu::getRoleId, roleIds);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Menu> getMenusByIds(List<Long> menuIds) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Menu::getId, menuIds)
                .eq(Menu::getStatus, 1)
                .ne(Menu::getMenuType, 3)
                .orderByAsc(Menu::getSort);
        return this.list(wrapper);
    }

    private List<UserMenuVO> buildMenuTree(List<Menu> menus, Long parentId) {
        List<UserMenuVO> result = new ArrayList<>();
        
        for (Menu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                UserMenuVO vo = convertToVO(menu);
                vo.setChildren(buildMenuTree(menus, menu.getId()));
                result.add(vo);
            }
        }
        
        return result;
    }

    private UserMenuVO convertToVO(Menu menu) {
        UserMenuVO vo = new UserMenuVO();
        vo.setId(menu.getId());
        vo.setMenuName(menu.getMenuName());
        vo.setParentId(menu.getParentId());
        vo.setMenuType(menu.getMenuType());
        vo.setRoutePath(menu.getRoutePath());
        vo.setName(generateRouteName(menu));
        vo.setComponentPath(menu.getComponentPath());
        vo.setPermission(menu.getPermission());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());
        vo.setStatus(menu.getStatus());
        vo.setHidden(0);
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    private String generateRouteName(Menu menu) {
        if (menu.getRoutePath() != null && !menu.getRoutePath().isEmpty()) {
            String path = menu.getRoutePath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path.replace("/", "-") + "-" + menu.getId();
        }
        return "menu-" + menu.getId();
    }
}