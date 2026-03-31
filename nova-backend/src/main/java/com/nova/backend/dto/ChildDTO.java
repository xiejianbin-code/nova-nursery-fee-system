package com.nova.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 幼儿数据传输对象
 *
 * @author Nova
 */
@Data
public class ChildDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（更新时需要）
     */
    private Long id;

    /**
     * 幼儿姓名
     */
    @NotBlank(message = "幼儿姓名不能为空")
    private String name;

    /**
     * 性别：MALE-男, FEMALE-女
     */
    @NotBlank(message = "性别不能为空")
    private String gender;

    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不能为空")
    private LocalDate birthDate;

    /**
     * 班级ID
     */
    @NotNull(message = "班级不能为空")
    private Long classId;

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
    @NotBlank(message = "家长姓名不能为空")
    private String parentName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
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
     * 照片URL
     */
    private String photo;

}
