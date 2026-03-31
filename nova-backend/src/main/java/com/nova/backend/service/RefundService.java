package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.RefundDTO;
import com.nova.backend.dto.RefundQueryDTO;
import com.nova.backend.vo.RefundVO;

import java.util.List;

/**
 * 退费服务接口
 *
 * @author Nova
 */
public interface RefundService {

    /**
     * 分页查询退费记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<RefundVO> pageList(RefundQueryDTO queryDTO);

    /**
     * 退费登记
     *
     * @param dto 退费信息
     * @return 是否成功
     */
    boolean register(RefundDTO dto);

    /**
     * 查询幼儿退费记录
     *
     * @param childId 幼儿ID
     * @return 退费记录列表
     */
    List<RefundVO> getByChildId(Long childId);
}
