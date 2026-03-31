package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 合同统计报表响应对象
 *
 * @author Nova
 */
@Data
public class ContractReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合同总数
     */
    private Integer totalContracts;

    /**
     * 生效中合同数
     */
    private Integer activeContracts;

    /**
     * 已过期合同数
     */
    private Integer expiredContracts;

    /**
     * 已变更合同数
     */
    private Integer changedContracts;

    /**
     * 已终止合同数
     */
    private Integer terminatedContracts;

    /**
     * 续签率
     */
    private Double renewedRate;

    /**
     * 按月收费合同数
     */
    private Integer monthlyCount;

    /**
     * 按学期收费合同数
     */
    private Integer semesterCount;

    /**
     * 本月新增合同数
     */
    private Integer newContractThisMonth;

    /**
     * 本月到期合同数
     */
    private Integer expireContractThisMonth;
}
