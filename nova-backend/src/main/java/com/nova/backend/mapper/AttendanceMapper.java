package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.Attendance;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 出勤记录Mapper
 *
 * @author Nova
 */
public interface AttendanceMapper extends BaseMapper<Attendance> {

    /**
     * 统计幼儿某月某状态的出勤天数
     *
     * @param childId 幼儿ID
     * @param year 年份
     * @param month 月份
     * @param status 出勤状态
     * @return 出勤天数
     */
    @Select("SELECT COUNT(*) FROM t_attendance WHERE child_id = #{childId} " +
            "AND YEAR(attendance_date) = #{year} AND MONTH(attendance_date) = #{month} " +
            "AND status = #{status} AND deleted = 0")
    Integer countByChildAndMonth(@Param("childId") Long childId, 
                                   @Param("year") Integer year, 
                                   @Param("month") Integer month, 
                                   @Param("status") Integer status);
}