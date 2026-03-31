package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.PaymentDTO;
import com.nova.backend.dto.PaymentQueryDTO;
import com.nova.backend.vo.PaymentVO;

import java.util.List;

/**
 * 收费服务接口
 *
 * @author Nova
 */
public interface PaymentService {

    /**
     * 分页查询收费记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<PaymentVO> pageList(PaymentQueryDTO queryDTO);

    /**
     * 收费登记
     *
     * @param dto 收费信息
     * @return 是否成功
     */
    boolean register(PaymentDTO dto);

    /**
     * 作废收费记录
     *
     * @param id 收费记录ID
     * @param reason 作废原因
     * @return 是否成功
     */
    boolean voidPayment(Long id, String reason);

    /**
     * 查询幼儿收费记录
     *
     * @param childId 幼儿ID
     * @return 收费记录列表
     */
    List<PaymentVO> getByChildId(Long childId);
}
