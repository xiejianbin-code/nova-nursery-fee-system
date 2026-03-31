package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.BalanceRecordQueryDTO;
import com.nova.backend.vo.BalanceRecordVO;
import com.nova.backend.vo.BalanceVO;

import java.math.BigDecimal;

/**
 * 余额服务接口
 *
 * @author Nova
 */
public interface BalanceService {

    /**
     * 查询账户余额
     *
     * @param childId 幼儿ID
     * @param accountType 账户类型
     * @return 账户余额
     */
    BigDecimal getBalance(Long childId, String accountType);

    /**
     * 查询幼儿三账户余额
     *
     * @param childId 幼儿ID
     * @return 三账户余额信息
     */
    BalanceVO getAllBalances(Long childId);

    /**
     * 增加余额
     *
     * @param childId 幼儿ID
     * @param accountType 账户类型
     * @param amount 金额
     * @param relatedNo 关联单号
     * @param feeItem 费用项目
     * @param remark 备注
     * @return 是否成功
     */
    boolean addBalance(Long childId, String accountType, BigDecimal amount, 
                       String relatedNo, String feeItem, String remark);

    /**
     * 扣减余额
     *
     * @param childId 幼儿ID
     * @param accountType 账户类型
     * @param amount 金额
     * @param relatedNo 关联单号
     * @param feeItem 费用项目
     * @param remark 备注
     * @return 是否成功
     */
    boolean deductBalance(Long childId, String accountType, BigDecimal amount,
                          String relatedNo, String feeItem, String remark);

    /**
     * 查询余额变动记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<BalanceRecordVO> getBalanceRecords(BalanceRecordQueryDTO queryDTO);
}
