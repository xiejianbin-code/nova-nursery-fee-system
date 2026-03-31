package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.BillBatchGenerateDTO;
import com.nova.backend.dto.BillDiscountDTO;
import com.nova.backend.dto.BillGenerateDTO;
import com.nova.backend.dto.BillOtherFeeDTO;
import com.nova.backend.dto.BillQueryDTO;
import com.nova.backend.vo.BillVO;

import java.util.List;

/**
 * 账单服务接口
 *
 * @author Nova
 */
public interface BillService {

    /**
     * 分页查询账单列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<BillVO> pageList(BillQueryDTO queryDTO);

    /**
     * 获取账单详情
     *
     * @param id 账单ID
     * @return 账单详情
     */
    BillVO getDetailById(Long id);

    /**
     * 获取账单明细列表
     *
     * @param billId 账单ID
     * @return 明细列表
     */
    List<BillVO.BillItemVO> getBillItems(Long billId);

    /**
     * 手动生成账单
     *
     * @param dto         生成账单参数
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean generateBill(BillGenerateDTO dto, Long currentUserId);

    /**
     * 批量生成账单
     *
     * @param dto 批量生成参数
     * @return 生成的账单数量
     */
    int batchGenerateBill(BillBatchGenerateDTO dto);

    /**
     * 自动生成上月账单（定时任务调用）
     *
     * @return 生成的账单数量
     */
    int generateMonthlyBills();

    /**
     * 确认账单
     *
     * @param id           账单ID
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean confirmBill(Long id, Long currentUserId);

    /**
     * 重新核算账单
     *
     * @param id           账单ID
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean recalculateBill(Long id, Long currentUserId);

    /**
     * 账单减免
     *
     * @param dto          减免参数
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean discountBill(BillDiscountDTO dto, Long currentUserId);

    /**
     * 手动添加其他费用
     *
     * @param dto          其他费用参数
     * @param currentUserId 当前用户ID
     * @return 是否成功
     */
    boolean addOtherFee(BillOtherFeeDTO dto, Long currentUserId);

    /**
     * 导出账单
     *
     * @param ids 账单ID列表
     * @return 导出数据
     */
    byte[] exportBills(List<Long> ids);

}
