package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.backend.dto.ContractChangeDTO;
import com.nova.backend.dto.ContractDTO;
import com.nova.backend.dto.ContractQueryDTO;
import com.nova.backend.dto.ContractRenewDTO;
import com.nova.backend.vo.ContractVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 合同服务接口
 *
 * @author Nova
 */
public interface ContractService {

    /**
     * 分页查询合同列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<ContractVO> pageList(ContractQueryDTO queryDTO);

    /**
     * 获取合同选项列表（不分页）
     *
     * @return 合同列表
     */
    List<ContractVO> listAll();

    /**
     * 获取合同详情（包含年龄段明细、其他费用、变更历史）
     *
     * @param id 合同ID
     * @return 合同详情
     */
    ContractVO getDetailById(Long id);

    /**
     * 新签合同
     *
     * @param dto 合同数据传输对象
     * @return 是否成功
     */
    boolean create(ContractDTO dto);

    /**
     * 合同变更
     * 新合同从下一个自然月1日起生效，当前月账单仍按原合同核算
     *
     * @param dto 合同变更数据传输对象
     * @return 是否成功
     */
    boolean changeContract(ContractChangeDTO dto);

    /**
     * 终止合同
     * 幼儿退学/毕业时终止合同，停止生成后续账单
     *
     * @param id     合同ID
     * @param reason 终止原因
     * @return 是否成功
     */
    boolean terminate(Long id, String reason);

    /**
     * 续签合同
     *
     * @param dto 合同续签数据传输对象
     * @return 是否成功
     */
    boolean renew(ContractRenewDTO dto);

    /**
     * 获取幼儿当前生效合同
     *
     * @param childId 幼儿ID
     * @return 合同详情
     */
    ContractVO getActiveContractByChildId(Long childId);

    /**
     * 根据月龄获取收费标准
     *
     * @param contractId 合同ID
     * @param ageMonths  幼儿月龄
     * @return 收费标准（保教费+伙食费）
     */
    BigDecimal getFeeByAge(Long contractId, Integer ageMonths);
}
