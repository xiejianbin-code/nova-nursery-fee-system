-- 添加系统配置菜单
INSERT INTO `t_menu` (`id`, `menu_name`, `parent_id`, `menu_type`, `route_path`, `component_path`, `permission`, `icon`, `sort`, `status`, `create_time`, `create_user`, `update_time`, `update_user`, `deleted`)
VALUES (35, '系统配置', 1, 2, '/system/config', 'system/config/index', 'system:config:list', 'Setting', 6, 1, NOW(), NULL, NOW(), NULL, 0);

-- 为admin角色添加系统配置菜单权限
INSERT INTO `t_role_menu` (`role_id`, `menu_id`) VALUES (1, 35);
