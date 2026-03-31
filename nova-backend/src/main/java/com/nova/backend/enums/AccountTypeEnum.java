package com.nova.backend.enums;

import lombok.Getter;

/**
 * 账户类型枚举
 *
 * @author Nova
 */
@Getter
public enum AccountTypeEnum {
    
    EDUCATION("EDUCATION", "保教费账户"),
    MEAL("MEAL", "伙食费账户"),
    OTHER("OTHER", "其他费用账户");
    
    private final String code;
    private final String name;
    
    AccountTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static AccountTypeEnum getByCode(String code) {
        for (AccountTypeEnum type : values()) {
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
        AccountTypeEnum type = getByCode(code);
        return type != null ? type.getName() : code;
    }
}