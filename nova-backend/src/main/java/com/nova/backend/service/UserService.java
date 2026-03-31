package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.backend.dto.CurrentUserVO;
import com.nova.backend.dto.LoginDTO;
import com.nova.backend.dto.LoginVO;
import com.nova.backend.dto.UserQueryDTO;
import com.nova.backend.entity.User;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author Nova
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录响应
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户
     *
     * @return 用户实体
     */
    User getCurrentUser();

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息VO
     */
    CurrentUserVO getCurrentUserInfo();

    /**
     * 分页查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Object pageList(UserQueryDTO queryDTO);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    Object getUserDetail(Long id);

    /**
     * 新增用户
     *
     * @param user 用户信息
     */
    void addUser(User user);

    /**
     * 修改用户
     *
     * @param user 用户信息
     */
    void updateUser(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取当前用户权限
     *
     * @return 权限列表
     */
    List<String> getCurrentUserPermissions();

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param id 用户ID
     */
    void resetPassword(Long id);

    /**
     * 获取老师列表（角色为TEACHER的用户）
     *
     * @return 老师列表
     */
    List<User> getTeacherList();

    /**
     * 更新当前用户信息
     *
     * @param user 用户信息
     */
    void updateCurrentUserInfo(User user);
}