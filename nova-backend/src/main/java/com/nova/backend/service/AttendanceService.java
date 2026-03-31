package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.backend.dto.AttendanceBatchDTO;
import com.nova.backend.dto.AttendanceDTO;
import com.nova.backend.entity.Attendance;
import com.nova.backend.vo.AttendanceStatisticsVO;
import com.nova.backend.vo.AttendanceVO;

import java.util.List;

/**
 * 出勤记录服务接口
 *
 * @author Nova
 */
public interface AttendanceService extends IService<Attendance> {

    /**
     * 分页查询出勤记录
     *
     * @param page 页码
     * @param limit 每页数量
     * @param childId 幼儿ID
     * @param classId 班级ID
     * @param date 日期
     * @return 分页结果
     */
    Object pageList(Integer page, Integer limit, Long childId, Long classId, String date);

    /**
     * 出勤登记
     *
     * @param dto 出勤登记信息
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean register(AttendanceDTO dto, Long userId);

    /**
     * 批量出勤登记
     *
     * @param dto 批量出勤登记信息
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean batchRegister(AttendanceBatchDTO dto, Long userId);

    /**
     * 修改出勤记录
     *
     * @param dto 出勤信息
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean modify(AttendanceDTO dto, Long userId);

    /**
     * 查询幼儿某月出勤记录
     *
     * @param childId 幼儿ID
     * @param year 年份
     * @param month 月份
     * @return 出勤记录列表
     */
    List<AttendanceVO> getByChildAndMonth(Long childId, int year, int month);

    /**
     * 查询班级某月出勤记录
     *
     * @param classId 班级ID
     * @param year 年份
     * @param month 月份
     * @return 出勤记录列表
     */
    List<AttendanceVO> getByClassAndMonth(Long classId, int year, int month);

    /**
     * 获取幼儿某月出勤统计
     *
     * @param childId 幼儿ID
     * @param year 年份
     * @param month 月份
     * @return 出勤统计
     */
    AttendanceStatisticsVO getStatistics(Long childId, int year, int month);

    /**
     * 获取班级某月出勤统计
     *
     * @param classId 班级ID
     * @param year 年份
     * @param month 月份
     * @return 出勤统计列表
     */
    List<AttendanceStatisticsVO> getClassStatistics(Long classId, int year, int month);

    /**
     * 导出班级某月出勤记录
     *
     * @param classId 班级ID
     * @param year 年份
     * @param month 月份
     * @return Excel数据
     */
    byte[] export(Long classId, Integer year, Integer month);
}