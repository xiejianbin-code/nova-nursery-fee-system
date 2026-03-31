package com.nova.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.backend.dto.AttendanceBatchDTO;
import com.nova.backend.dto.AttendanceDTO;
import com.nova.backend.entity.Attendance;
import com.nova.backend.entity.Child;
import com.nova.backend.entity.Clazz;
import com.nova.backend.entity.User;
import com.nova.backend.mapper.AttendanceMapper;
import com.nova.backend.mapper.ChildMapper;
import com.nova.backend.mapper.ClazzMapper;
import com.nova.backend.mapper.UserMapper;
import com.nova.backend.service.AttendanceService;
import com.nova.backend.vo.AttendanceStatisticsVO;
import com.nova.backend.vo.AttendanceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 出勤记录服务实现
 *
 * @author Nova
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    private final ChildMapper childMapper;
    private final ClazzMapper clazzMapper;
    private final UserMapper userMapper;

    @Override
    public Object pageList(Integer page, Integer limit, Long childId, Long classId, String date) {
        Page<Attendance> pageInfo = new Page<>(page, limit);
        
        LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
        
        if (childId != null) {
            queryWrapper.eq(Attendance::getChildId, childId);
        } else if (classId != null) {
            List<Child> children = childMapper.selectList(
                new LambdaQueryWrapper<Child>().eq(Child::getClassId, classId)
            );
            if (!children.isEmpty()) {
                List<Long> childIds = children.stream().map(Child::getId).collect(Collectors.toList());
                queryWrapper.in(Attendance::getChildId, childIds);
            } else {
                return pageInfo;
            }
        }
        
        if (date != null && !date.isEmpty()) {
            queryWrapper.eq(Attendance::getAttendanceDate, LocalDate.parse(date));
        }
        
        queryWrapper.orderByDesc(Attendance::getAttendanceDate);
        
        Page<Attendance> result = this.page(pageInfo, queryWrapper);
        List<AttendanceVO> voList = convertToVOList(result.getRecords());
        result.setRecords(new ArrayList<>());
        
        Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap.put("records", voList);
        resultMap.put("total", result.getTotal());
        resultMap.put("size", result.getSize());
        resultMap.put("current", result.getCurrent());
        resultMap.put("pages", result.getPages());
        
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(AttendanceDTO dto, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        
        LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attendance::getChildId, dto.getChildId())
                   .eq(Attendance::getAttendanceDate, dto.getAttendanceDate());
        
        Attendance existingAttendance = this.getOne(queryWrapper);
        
        if (existingAttendance != null) {
            existingAttendance.setStatus(dto.getStatus());
            existingAttendance.setModifyUser(userId);
            existingAttendance.setUpdateTime(now);
            existingAttendance.setUpdateUser(userId);
            return this.updateById(existingAttendance);
        } else {
            Attendance attendance = new Attendance();
            BeanUtils.copyProperties(dto, attendance);
            attendance.setRegisterUser(userId);
            attendance.setCreateTime(now);
            attendance.setCreateUser(userId);
            attendance.setUpdateTime(now);
            attendance.setUpdateUser(userId);
            return this.save(attendance);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRegister(AttendanceBatchDTO dto, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        
        Map<Long, Integer> childStatusMap = new java.util.HashMap<>();
        
        if (dto.getChildIds() != null) {
            for (Long childId : dto.getChildIds()) {
                childStatusMap.put(childId, 1);
            }
        }
        
        if (dto.getLeaveChildIds() != null) {
            for (Long childId : dto.getLeaveChildIds()) {
                childStatusMap.put(childId, 2);
            }
        }
        
        if (dto.getSickChildIds() != null) {
            for (Long childId : dto.getSickChildIds()) {
                childStatusMap.put(childId, 3);
            }
        }
        
        if (dto.getAbsentChildIds() != null) {
            for (Long childId : dto.getAbsentChildIds()) {
                childStatusMap.put(childId, 4);
            }
        }
        
        for (Map.Entry<Long, Integer> entry : childStatusMap.entrySet()) {
            Long childId = entry.getKey();
            Integer status = entry.getValue();
            
            LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Attendance::getChildId, childId)
                       .eq(Attendance::getAttendanceDate, dto.getAttendanceDate());
            
            Attendance existingAttendance = this.getOne(queryWrapper);
            
            if (existingAttendance != null) {
                existingAttendance.setStatus(status);
                existingAttendance.setModifyUser(userId);
                existingAttendance.setUpdateTime(now);
                existingAttendance.setUpdateUser(userId);
                this.updateById(existingAttendance);
            } else {
                Attendance attendance = new Attendance();
                attendance.setChildId(childId);
                attendance.setAttendanceDate(dto.getAttendanceDate());
                attendance.setStatus(status);
                attendance.setRegisterUser(userId);
                attendance.setCreateTime(now);
                attendance.setCreateUser(userId);
                attendance.setUpdateTime(now);
                attendance.setUpdateUser(userId);
                this.save(attendance);
            }
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modify(AttendanceDTO dto, Long userId) {
        if (dto.getId() == null) {
            return false;
        }
        
        Attendance existingAttendance = this.getById(dto.getId());
        if (existingAttendance == null) {
            return false;
        }
        
        existingAttendance.setStatus(dto.getStatus());
        if (dto.getRemark() != null) {
            existingAttendance.setRemark(dto.getRemark());
        }
        existingAttendance.setModifyUser(userId);
        existingAttendance.setUpdateTime(LocalDateTime.now());
        existingAttendance.setUpdateUser(userId);
        
        return this.updateById(existingAttendance);
    }

    @Override
    public List<AttendanceVO> getByChildAndMonth(Long childId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        
        LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attendance::getChildId, childId)
                   .between(Attendance::getAttendanceDate, startDate, endDate)
                   .orderByAsc(Attendance::getAttendanceDate);
        
        List<Attendance> list = this.list(queryWrapper);
        return convertToVOList(list);
    }

    @Override
    public List<AttendanceVO> getByClassAndMonth(Long classId, int year, int month) {
        List<Child> children = childMapper.selectList(
            new LambdaQueryWrapper<Child>().eq(Child::getClassId, classId)
        );
        
        if (children.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> childIds = children.stream().map(Child::getId).collect(Collectors.toList());
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        
        LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Attendance::getChildId, childIds)
                   .between(Attendance::getAttendanceDate, startDate, endDate)
                   .orderByAsc(Attendance::getAttendanceDate)
                   .orderByAsc(Attendance::getChildId);
        
        List<Attendance> list = this.list(queryWrapper);
        return convertToVOList(list);
    }

    @Override
    public AttendanceStatisticsVO getStatistics(Long childId, int year, int month) {
        AttendanceStatisticsVO vo = new AttendanceStatisticsVO();
        vo.setChildId(childId);
        vo.setYear(year);
        vo.setMonth(month);
        
        Child child = childMapper.selectById(childId);
        if (child != null) {
            vo.setChildName(child.getName());
            vo.setClassId(child.getClassId());
            if (child.getClassId() != null) {
                Clazz clazz = clazzMapper.selectById(child.getClassId());
                if (clazz != null) {
                    vo.setClassName(clazz.getClassName());
                }
            }
        }
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        
        LambdaQueryWrapper<Attendance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attendance::getChildId, childId)
                   .between(Attendance::getAttendanceDate, startDate, endDate);
        
        List<Attendance> list = this.list(queryWrapper);
        
        int normalDays = 0;
        int leaveDays = 0;
        int sickDays = 0;
        int absentDays = 0;
        
        for (Attendance attendance : list) {
            Integer status = attendance.getStatus();
            if (status != null) {
                if (status == 1) {
                    normalDays++;
                } else if (status == 2) {
                    leaveDays++;
                } else if (status == 3) {
                    sickDays++;
                } else if (status == 4) {
                    absentDays++;
                }
            }
        }
        
        vo.setTotalDays(normalDays + leaveDays + sickDays + absentDays);
        vo.setNormalDays(normalDays);
        vo.setLeaveDays(leaveDays);
        vo.setSickDays(sickDays);
        vo.setAbsentDays(absentDays);
        
        if (vo.getTotalDays() > 0) {
            vo.setAttendanceRate((double) normalDays / vo.getTotalDays() * 100);
        } else {
            vo.setAttendanceRate(0.0);
        }
        
        return vo;
    }

    @Override
    public List<AttendanceStatisticsVO> getClassStatistics(Long classId, int year, int month) {
        List<Child> children = childMapper.selectList(
            new LambdaQueryWrapper<Child>().eq(Child::getClassId, classId)
        );
        
        if (children.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<AttendanceStatisticsVO> resultList = new ArrayList<>();
        
        for (Child child : children) {
            AttendanceStatisticsVO vo = getStatistics(child.getId(), year, month);
            resultList.add(vo);
        }
        
        return resultList;
    }

    @Override
    public byte[] export(Long classId, Integer year, Integer month) {
        List<AttendanceVO> attendanceList = getByClassAndMonth(classId, year, month);
        
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("出勤记录");
            
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"幼儿姓名", "班级", "日期", "出勤状态", "登记人", "登记时间", "修改人", "修改时间"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            for (int i = 0; i < attendanceList.size(); i++) {
                AttendanceVO vo = attendanceList.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(vo.getChildName() != null ? vo.getChildName() : "");
                row.createCell(1).setCellValue(vo.getClassName() != null ? vo.getClassName() : "");
                row.createCell(2).setCellValue(vo.getAttendanceDate() != null ? vo.getAttendanceDate().toString() : "");
                row.createCell(3).setCellValue(getStatusDesc(vo.getStatus()));
                row.createCell(4).setCellValue(vo.getRegisterUserName() != null ? vo.getRegisterUserName() : "");
                row.createCell(5).setCellValue(vo.getRegisterTime() != null ? vo.getRegisterTime().toString() : "");
                row.createCell(6).setCellValue(vo.getModifyUserName() != null ? vo.getModifyUserName() : "");
                row.createCell(7).setCellValue(vo.getModifyTime() != null ? vo.getModifyTime().toString() : "");
            }
            
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    private List<AttendanceVO> convertToVOList(List<Attendance> list) {
        List<AttendanceVO> voList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return voList;
        }

        List<Long> childIds = list.stream().map(Attendance::getChildId).distinct().collect(Collectors.toList());
        Map<Long, Child> childMap = childMapper.selectBatchIds(childIds).stream()
            .collect(Collectors.toMap(Child::getId, child -> child));
        
        List<Long> classIds = childMap.values().stream()
            .map(Child::getClassId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, Clazz> clazzMap = clazzMapper.selectBatchIds(classIds).stream()
            .collect(Collectors.toMap(Clazz::getId, clazz -> clazz));
        
        List<Long> userIds = list.stream()
            .flatMap(a -> java.util.stream.Stream.of(
                a.getRegisterUser() != null ? a.getRegisterUser() : null,
                a.getModifyUser() != null ? a.getModifyUser() : null
            ))
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
            .collect(Collectors.toMap(User::getId, user -> user));
        
        for (Attendance attendance : list) {
            AttendanceVO vo = new AttendanceVO();
            BeanUtils.copyProperties(attendance, vo);
            
            Child child = childMap.get(attendance.getChildId());
            if (child != null) {
                vo.setChildName(child.getName());
                vo.setClassId(child.getClassId());
                Clazz clazz = clazzMap.get(child.getClassId());
                if (clazz != null) {
                    vo.setClassName(clazz.getClassName());
                }
            }
            
            vo.setStatusDesc(getStatusDesc(attendance.getStatus()));
            
            if (attendance.getRegisterUser() != null) {
                User registerUser = userMap.get(attendance.getRegisterUser());
                if (registerUser != null) {
                    vo.setRegisterUserName(registerUser.getUsername());
                }
            }
            vo.setRegisterTime(attendance.getCreateTime());
            
            if (attendance.getModifyUser() != null) {
                User modifyUser = userMap.get(attendance.getModifyUser());
                if (modifyUser != null) {
                    vo.setModifyUserName(modifyUser.getUsername());
                }
            }
            vo.setModifyTime(attendance.getUpdateTime());
            
            voList.add(vo);
        }
        
        return voList;
    }

    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case 1:
                return "正常到园";
            case 2:
                return "请假";
            case 3:
                return "病假";
            case 4:
                return "缺勤";
            default:
                return "";
        }
    }
}