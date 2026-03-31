package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 账单Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 根据幼儿ID和账单月份查询账单
     *
     * @param childId   幼儿ID
     * @param billMonth 账单月份
     * @return 账单
     */
    @Select("SELECT * FROM t_bill WHERE child_id = #{childId} AND bill_month = #{billMonth} AND deleted = 0")
    Bill selectByChildIdAndMonth(@Param("childId") Long childId, @Param("billMonth") String billMonth);

    /**
     * 查询幼儿某年所有账单
     *
     * @param childId 幼儿ID
     * @param year    年份
     * @return 账单列表
     */
    @Select("SELECT * FROM t_bill WHERE child_id = #{childId} AND bill_month LIKE CONCAT(#{year}, '-%') AND deleted = 0 ORDER BY bill_month")
    List<Bill> selectByChildIdAndYear(@Param("childId") Long childId, @Param("year") Integer year);

    /**
     * 查询某月所有待确认账单
     *
     * @param billMonth 账单月份
     * @return 账单列表
     */
    @Select("SELECT * FROM t_bill WHERE bill_month = #{billMonth} AND status = 'PENDING' AND deleted = 0")
    List<Bill> selectPendingBillsByMonth(@Param("billMonth") String billMonth);

}
