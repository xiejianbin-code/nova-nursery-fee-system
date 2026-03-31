package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.AlertQueryDTO;
import com.nova.backend.entity.Alert;
import com.nova.backend.entity.Child;
import com.nova.backend.entity.Clazz;
import com.nova.backend.entity.User;
import com.nova.backend.exception.BusinessException;
import com.nova.backend.mapper.AlertMapper;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.mapper.ClazzMapper;
import com.nova.backend.mapper.UserMapper;
import com.nova.backend.service.AlertService;
import com.nova.backend.enums.AlertTypeEnum;
import com.nova.backend.vo.AlertVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警服务实现类
 *
 * @author Nova
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertMapper alertMapper;
    private final ChildMapper childMapper;
    private final ClazzMapper clazzMapper;
    private final UserMapper userMapper;

    private static final String ALERT_TYPE_CONTRACT_EXPIRE = "CONTRACT_EXPIRE";
    private static final String ALERT_TYPE_EDUCATION_FEE_EXPIRE = "EDUCATION_FEE_EXPIRE";
    private static final String ALERT_TYPE_MEAL_FEE_EXPIRE = "MEAL_FEE_EXPIRE";
    private static final String ALERT_TYPE_OVERDUE = "OVERDUE";
    private static final String ALERT_TYPE_EDUCATION_FEE_LOW = "EDUCATION_FEE_LOW";
    private static final String ALERT_TYPE_MEAL_FEE_LOW = "MEAL_FEE_LOW";
    private static final String ALERT_TYPE_OTHER_FEE_LOW = "OTHER_FEE_LOW";

    private static final String HANDLE_STATUS_PENDING = "PENDING";
    private static final String HANDLE_STATUS_HANDLED = "HANDLED";

    @Override
    public Page<AlertVO> pageList(AlertQueryDTO queryDTO) {
        Page<Alert> page = new Page<>(
                queryDTO.getPage() != null ? queryDTO.getPage() : 1,
                queryDTO.getSize() != null ? queryDTO.getSize() : 10
        );

        LambdaQueryWrapper<Alert> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryDTO.getChildId() != null, Alert::getChildId, queryDTO.getChildId())
                .eq(StringUtils.hasText(queryDTO.getAlertType()), Alert::getAlertType, queryDTO.getAlertType())
                .eq(StringUtils.hasText(queryDTO.getHandleStatus()), Alert::getHandleStatus, queryDTO.getHandleStatus())
                .ge(queryDTO.getTriggerTimeBegin() != null, Alert::getTriggerTime, queryDTO.getTriggerTimeBegin())
                .le(queryDTO.getTriggerTimeEnd() != null, Alert::getTriggerTime, queryDTO.getTriggerTimeEnd())
                .orderByDesc(Alert::getTriggerTime);

        Page<Alert> result = alertMapper.selectPage(page, queryWrapper);

        List<AlertVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        fillRelatedInfo(voList);

        Page<AlertVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Long getUnhandleCount() {
        LambdaQueryWrapper<Alert> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Alert::getHandleStatus, HANDLE_STATUS_PENDING);
        return alertMapper.selectCount(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handle(Long id, String remark) {
        Alert alert = alertMapper.selectById(id);
        if (alert == null) {
            throw new BusinessException("预警记录不存在");
        }
        if (HANDLE_STATUS_HANDLED.equals(alert.getHandleStatus())) {
            throw new BusinessException("该预警已处理");
        }

        LambdaUpdateWrapper<Alert> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Alert::getId, id)
                .set(Alert::getHandleStatus, HANDLE_STATUS_HANDLED)
                .set(Alert::getHandleTime, LocalDateTime.now())
                .set(Alert::getRemark, remark)
                .set(Alert::getUpdateTime, LocalDateTime.now());

        return alertMapper.update(null, updateWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAlert(Long childId, String type, String content) {
        Alert alert = new Alert();
        alert.setChildId(childId);
        alert.setAlertType(type);
        alert.setAlertContent(content);
        alert.setTriggerTime(LocalDateTime.now());
        alert.setHandleStatus(HANDLE_STATUS_PENDING);
        alert.setCreateTime(LocalDateTime.now());
        alert.setUpdateTime(LocalDateTime.now());
        return alertMapper.insert(alert) > 0;
    }

    private AlertVO convertToVO(Alert alert) {
        AlertVO vo = new AlertVO();
        BeanUtils.copyProperties(alert, vo);
        vo.setAlertTypeName(getAlertTypeName(alert.getAlertType()));
        vo.setHandleStatusName(getHandleStatusName(alert.getHandleStatus()));
        return vo;
    }

    private void fillRelatedInfo(List<AlertVO> voList) {
        if (voList == null || voList.isEmpty()) {
            return;
        }

        List<Long> childIds = voList.stream()
                .map(AlertVO::getChildId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> handleUserIds = voList.stream()
                .filter(vo -> vo.getHandleUser() != null)
                .map(AlertVO::getHandleUser)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Child> childMap = null;
        if (!childIds.isEmpty()) {
            List<Child> children = childMapper.selectBatchIds(childIds);
            childMap = children.stream()
                    .collect(Collectors.toMap(Child::getId, c -> c));
        }

        Map<Long, Clazz> clazzMap = null;
        if (childMap != null) {
            List<Long> classIds = childMap.values().stream()
                    .map(Child::getClassId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            if (!classIds.isEmpty()) {
                List<Clazz> clazzList = clazzMapper.selectBatchIds(classIds);
                clazzMap = clazzList.stream()
                        .collect(Collectors.toMap(Clazz::getId, c -> c));
            }
        }

        Map<Long, User> userMap = null;
        if (!handleUserIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(handleUserIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        }

        Map<Long, Child> finalChildMap = childMap;
        Map<Long, Clazz> finalClazzMap = clazzMap;
        Map<Long, User> finalUserMap = userMap;

        for (AlertVO vo : voList) {
            if (finalChildMap != null && vo.getChildId() != null) {
                Child child = finalChildMap.get(vo.getChildId());
                if (child != null) {
                    vo.setChildName(child.getName());
                    if (finalClazzMap != null && child.getClassId() != null) {
                        Clazz clazz = finalClazzMap.get(child.getClassId());
                        if (clazz != null) {
                            vo.setClassName(clazz.getClassName());
                        }
                    }
                }
            }
            if (finalUserMap != null && vo.getHandleUser() != null) {
                User user = finalUserMap.get(vo.getHandleUser());
                if (user != null) {
                    vo.setHandleUserName(user.getRealName());
                }
            }
        }
    }

    private String getAlertTypeName(String alertType) {
        if (alertType == null) {
            return "";
        }
        return AlertTypeEnum.getNameByCode(alertType);
    }

    private String getHandleStatusName(String handleStatus) {
        if (handleStatus == null) {
            return "";
        }
        switch (handleStatus) {
            case HANDLE_STATUS_PENDING:
                return "待处理";
            case HANDLE_STATUS_HANDLED:
                return "已处理";
            default:
                return handleStatus;
        }
    }
}