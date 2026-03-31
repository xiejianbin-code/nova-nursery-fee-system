package com.nova.backend.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 幼儿详情响应对象
 *
 * @author Nova
 */
@Data
public class ChildVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 幼儿姓名
     */
    private String name;

    /**
     * 性别：MALE-男, FEMALE-女
     */
    private String gender;

    /**
     * 性别描述
     */
    private String genderDesc;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 月龄（自动计算）
     */
    private Integer ageMonths;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 入园日期
     */
    private LocalDate enrollDate;

    /**
     * 合同到期日期
     */
    private LocalDate contractExpireDate;

    /**
     * 家长姓名
     */
    private String parentName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 接送授权人（JSON格式存储多个授权人）
     */
    private String pickupAuth;

    /**
     * 过敏信息
     */
    private String allergyInfo;

    /**
     * 状态：IN_GARDEN-在园, SUSPENDED-休学, DROPPED_OUT-退学, GRADUATED-毕业
     */
    private String status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 照片URL
     */
    private String photo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
