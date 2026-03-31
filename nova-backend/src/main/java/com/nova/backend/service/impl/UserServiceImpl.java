package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.backend.dto.CurrentUserVO;
import com.nova.backend.dto.LoginDTO;
import com.nova.backend.dto.LoginVO;
import com.nova.backend.dto.UserQueryDTO;
import com.nova.backend.dto.UserInfoVO;
import com.nova.backend.dto.UserVO;
import com.nova.backend.entity.Role;
import com.nova.backend.entity.User;
import com.nova.backend.entity.UserRole;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.RoleMapper;
import com.nova.backend.mapper.UserMapper;
import com.nova.backend.mapper.UserRoleMapper;
import com.nova.backend.service.UserService;
import com.nova.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int LOCK_HOURS = 1;
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("账号已被锁定，请稍后再试");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            handleLoginFail(user);
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getLoginFailCount() > 0 || user.getLockTime() != null) {
            user.setLoginFailCount(0);
            user.setLockTime(null);
            this.updateById(user);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        setAuthentication(user);

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setRealName(user.getRealName());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setRoles(getUserRoles(user.getId()));
        userInfoVO.setPermissions(getUserPermissions(user.getId()));

        return new LoginVO(token, userInfoVO);
    }

    private void setAuthentication(User user) {
        List<String> roles = getUserRoles(user.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @Override
    public CurrentUserVO getCurrentUserInfo() {
        User user = getCurrentUser();
        if (user == null) {
            throw new BusinessException("用户未登录");
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setRealName(user.getRealName());
        userInfoVO.setPhone(user.getPhone());

        List<String> roles = getUserRoles(user.getId());
        List<String> roleNames = getUserRoleNames(user.getId());
        List<String> permissions = getUserPermissions(user.getId());

        CurrentUserVO currentUserVO = new CurrentUserVO();
        currentUserVO.setUser(userInfoVO);
        currentUserVO.setRoles(roles);
        currentUserVO.setRoleNames(roleNames);
        currentUserVO.setPermissions(permissions);

        return currentUserVO;
    }

    private void handleLoginFail(User user) {
        int failCount = user.getLoginFailCount() + 1;
        user.setLoginFailCount(failCount);

        if (failCount >= MAX_LOGIN_FAIL_COUNT) {
            user.setLockTime(LocalDateTime.now().plusHours(LOCK_HOURS));
            log.warn("用户 {} 登录失败次数达到 {} 次，账号锁定 {} 小时", user.getUsername(), MAX_LOGIN_FAIL_COUNT, LOCK_HOURS);
        }

        this.updateById(user);
    }

    private List<String> getUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        
        return roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
    }

    private List<String> getUserRoleNames(Long userId) {
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }

    private List<String> getUserPermissions(Long userId) {
        return new ArrayList<>();
    }

    @Override
    public Object pageList(UserQueryDTO queryDTO) {
        Page<User> page = new Page<>(queryDTO.getPage(), queryDTO.getLimit());
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
            queryWrapper.like(User::getUsername, queryDTO.getUsername());
        }
        if (queryDTO.getRealName() != null && !queryDTO.getRealName().isEmpty()) {
            queryWrapper.like(User::getRealName, queryDTO.getRealName());
        }
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(User::getStatus, queryDTO.getStatus());
        }
        
        queryWrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = this.page(page, queryWrapper);
        
        Page<UserVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UserVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId())
        );
        
        if (!userRoles.isEmpty()) {
            List<Long> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());
            vo.setRoleIds(roleIds);
            
            List<Role> roles = roleMapper.selectBatchIds(roleIds);
            List<String> roleNames = roles.stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
            vo.setRoleNames(roleNames);
        } else {
            vo.setRoleIds(new ArrayList<>());
            vo.setRoleNames(new ArrayList<>());
        }
        
        return vo;
    }

    @Override
    public Object getUserDetail(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        if (this.count(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setStatus(1);
        user.setLoginFailCount(0);
        
        this.save(user);
        
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            saveUserRoles(user.getId(), user.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        User existUser = this.getById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!existUser.getUsername().equals(user.getUsername())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, user.getUsername());
            queryWrapper.ne(User::getId, user.getId());
            if (this.count(queryWrapper) > 0) {
                throw new BusinessException("用户名已存在");
            }
        }
        
        user.setPassword(null);
        this.updateById(user);
        
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getId())
        );
        
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            saveUserRoles(user.getId(), user.getRoleIds());
        }
    }
    
    private void saveUserRoles(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除超级管理员");
        }
        
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, id)
        );
        
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能停用超级管理员");
        }
        
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public List<String> getCurrentUserPermissions() {
        User user = getCurrentUser();
        if (user == null) {
            return new ArrayList<>();
        }
        return getUserPermissions(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String oldPassword, String newPassword) {
        User user = getCurrentUser();
        if (user == null) {
            throw new BusinessException("用户未登录");
        }
        
        User existUser = this.getById(user.getId());
        if (!passwordEncoder.matches(oldPassword, existUser.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        
        existUser.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(existUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setLoginFailCount(0);
        user.setLockTime(null);
        this.updateById(user);
    }

    @Override
    public List<User> getTeacherList() {
        Role teacherRole = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, "TEACHER")
        );
        
        if (teacherRole == null) {
            return new ArrayList<>();
        }
        
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, teacherRole.getId())
        );
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> userIds = userRoles.stream()
                .map(UserRole::getUserId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        queryWrapper.eq(User::getStatus, 1);
        queryWrapper.orderByAsc(User::getRealName);
        
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentUserInfo(User user) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("用户未登录");
        }
        
        User existUser = this.getById(currentUser.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        existUser.setRealName(user.getRealName());
        existUser.setPhone(user.getPhone());
        this.updateById(existUser);
    }
}