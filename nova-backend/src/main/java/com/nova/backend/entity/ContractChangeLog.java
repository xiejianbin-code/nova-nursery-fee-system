package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 合同变更记录实体类
 *
 * @author Nova
 */
@Data
@TableName("t_contract_change_log")
public class ContractChangeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 变更类型：CHANGE-变更, TERMINATE-终止, RENEW-续签
     */
    private String changeType;

    /**
     * 变更原因
     */
    private String changeReason;

    /**
     * 变更前内容（JSON格式）
     */
    private String beforeContent;

    /**
     * 变更后内容（JSON格式）
     */
    private String afterContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createUser;
}
