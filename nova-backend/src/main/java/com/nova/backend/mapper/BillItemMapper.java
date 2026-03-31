package com.nova.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.backend.entity.BillItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 账单明细Mapper接口
 *
 * @author Nova
 */
@Mapper
public interface BillItemMapper extends BaseMapper<BillItem> {

    /**
     * 根据账单ID查询明细列表
     *
     * @param billId 账单ID
     * @return 明细列表
     */
    @Select("SELECT * FROM t_bill_item WHERE bill_id = #{billId} AND deleted = 0 ORDER BY id")
    List<BillItem> selectByBillId(@Param("billId") Long billId);

}
