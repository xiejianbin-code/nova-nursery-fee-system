package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.backend.dto.UserMenuVO;
import com.nova.backend.entity.Menu;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author Nova
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取用户菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<UserMenuVO> getUserMenus(Long userId);

    /**
     * 获取菜单树
     *
     * @return 菜单树
     */
    List<UserMenuVO> getMenuTree();

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void deleteMenu(Long id);
}