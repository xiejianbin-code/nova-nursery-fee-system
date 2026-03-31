package com.nova.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.backend.entity.SystemConfig;

public interface SystemConfigService extends IService<SystemConfig> {
    
    String getConfigValue(String configKey);
    
    Integer getConfigValueAsInt(String configKey, Integer defaultValue);
}
