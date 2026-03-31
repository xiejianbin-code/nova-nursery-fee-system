package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.entity.Clazz;
import com.nova.backend.vo.ClazzVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 班级Mapper
 *
 * @author Nova
 */
public interface ClazzMapper extends BaseMapper<Clazz> {

    @Select("<script>" +
            "SELECT c.id, c.class_name as className, c.teacher_id as teacherId, " +
            "u.real_name as teacherName, u.phone as teacherPhone, " +
            "c.current_count as currentCount, c.status, c.remark, c.create_time as createTime " +
            "FROM t_class c " +
            "LEFT JOIN t_user u ON c.teacher_id = u.id AND u.deleted = 0 " +
            "WHERE c.deleted = 0 " +
            "<if test='className != null and className != \"\"'>" +
            "AND c.class_name LIKE CONCAT('%', #{className}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND c.status = #{status} " +
            "</if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    IPage<ClazzVO> selectClazzPage(Page<ClazzVO> page, 
                                    @Param("className") String className, 
                                    @Param("status") Integer status);

    @Select("SELECT c.id, c.class_name as className, c.teacher_id as teacherId, " +
            "u.real_name as teacherName, u.phone as teacherPhone, " +
            "c.current_count as currentCount, c.status, c.remark, c.create_time as createTime " +
            "FROM t_class c " +
            "LEFT JOIN t_user u ON c.teacher_id = u.id AND u.deleted = 0 " +
            "WHERE c.id = #{id} AND c.deleted = 0")
    ClazzVO selectClazzById(@Param("id") Long id);
}
