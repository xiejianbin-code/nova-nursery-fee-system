package com.nova.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 幼儿状态更新DTO
 *
 * @author Nova
 */
@Data
public class ChildStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幼儿ID
     */
    @NotNull(message = "幼儿ID不能为空")
    private Long id;

    /**
     * 状态：IN_GARDEN-在园, SUSPENDED-休学, DROPPED_OUT-退学, GRADUATED-毕业
     */
    @NotBlank(message = "状态不能为空")
    private String status;

    /**
     * 状态变更原因
     */
    private String reason;

}
