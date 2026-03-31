package com.nova.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JWT配置属性
 *
 * @author Nova
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT签名密钥
     */
    private String secret;

    /**
     * Token过期时间（毫秒）
     */
    private Long expiration;

    /**
     * Token请求头名称
     */
    private String header;

    /**
     * Token前缀
     */
    private String prefix;

    /**
     * 白名单路径列表
     */
    private List<String> whiteList;
}
