package com.nova.backend.common;

/**
 * 合同状态常量
 *
 * @author Nova
 */
public class ContractStatus {

    /**
     * 生效中
     */
    public static final Integer ACTIVE = 1;

    /**
     * 已到期
     */
    public static final Integer EXPIRED = 2;

    /**
     * 已变更
     */
    public static final Integer CHANGED = 3;

    /**
     * 已终止
     */
    public static final Integer TERMINATED = 4;

    /**
     * 获取状态文本
     */
    public static String getText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "生效中";
            case 2:
                return "已到期";
            case 3:
                return "已变更";
            case 4:
                return "已终止";
            default:
                return "未知";
        }
    }
}
