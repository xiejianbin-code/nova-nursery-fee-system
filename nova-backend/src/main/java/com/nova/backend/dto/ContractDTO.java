package com.nova.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 合同数据传输对象
 *
 * @author Nova
 */
@Data
public class ContractDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时必填）
     */
    private Long id;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 幼儿ID
     */
    @NotNull(message = "幼儿ID不能为空")
    private Long childId;

    /**
     * 合同名称
     */
    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    /**
     * 课程类型：MONTHLY-按月, SEMESTER-按学期
     */
    @NotBlank(message = "课程类型不能为空")
    private String courseType;

    /**
     * 合同开始日期
     */
    @NotNull(message = "合同开始日期不能为空")
    private LocalDate startDate;

    /**
     * 合同结束日期
     */
    @NotNull(message = "合同结束日期不能为空")
    private LocalDate endDate;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 年龄段收费明细列表
     */
    @Valid
    @NotNull(message = "年龄段收费明细不能为空")
    private List<ContractFeeItemDTO> feeItems;

    /**
     * 其他费用列表
     */
    @Valid
    private List<ContractOtherFeeDTO> otherFees;
}
