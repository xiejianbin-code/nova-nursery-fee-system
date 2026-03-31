package com.nova.backend.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 预警类型枚举
 *
 * @author Nova
 */
@Getter
public enum AlertTypeEnum {
    
    CONTRACT_EXPIRE("CONTRACT_EXPIRE", "合同到期提醒"),
    EDUCATION_FEE_EXPIRE("EDUCATION_FEE_EXPIRE", "保教费到期提醒"),
    MEAL_FEE_EXPIRE("MEAL_FEE_EXPIRE", "伙食费到期提醒"),
    OVERDUE("OVERDUE", "逾期未缴费提醒"),
    EDUCATION_FEE_LOW("EDUCATION_FEE_LOW", "保教费余额不足提醒"),
    MEAL_FEE_LOW("MEAL_FEE_LOW", "伙食费余额不足提醒"),
    OTHER_FEE_LOW("OTHER_FEE_LOW", "其他费用余额不足提醒");
    
    private final String code;
    private final String name;
    
    AlertTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static AlertTypeEnum getByCode(String code) {
        for (AlertTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据代码获取中文名称
     */
    public static String getNameByCode(String code) {
        AlertTypeEnum type = getByCode(code);
        return type != null ? type.getName() : code;
    }
    
    /**
     * 获取所有预警类型
     */
    public static List<AlertTypeInfo> getAllTypes() {
        List<AlertTypeInfo> types = new ArrayList<>();
        for (AlertTypeEnum type : values()) {
            types.add(new AlertTypeInfo(type.getCode(), type.getName()));
        }
        return types;
    }
    
    /**
     * 预警类型信息
     */
    @Getter
    public static class AlertTypeInfo {
        private final String code;
        private final String name;
        
        public AlertTypeInfo(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}