package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同实体类
 *
 * @author Nova
 */
@Data
@TableName("t_contract")
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 幼儿ID
     */
    private Long childId;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 课程类型：MONTHLY-按月, SEMESTER-按学期
     */
    private String courseType;

    /**
     * 合同开始日期
     */
    private LocalDate startDate;

    /**
     * 合同结束日期
     */
    private LocalDate endDate;

    /**
     * 合同状态：1-生效中, 2-已到期, 3-已变更, 4-已终止
     */
    private Integer status;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 删除标记：0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
