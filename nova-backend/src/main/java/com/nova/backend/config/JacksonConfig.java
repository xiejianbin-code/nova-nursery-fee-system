package com.nova.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * 全局处理 Long 类型转 String，解决前端精度丢失
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 全局配置：将 Long 类型序列化为 String
        SimpleModule simpleModule = new SimpleModule();
        // 处理 Long 类型
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        // 处理 long 基本类型（防止包装类和基本类型都漏了）
        simpleModule.addSerializer(long.class, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
