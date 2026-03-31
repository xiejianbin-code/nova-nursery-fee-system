package com.nova.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码测试类
 * 用于验证和生成BCrypt加密的密码
 */
public class PasswordTest {

    public static void main(String[] args) {
        // 创建密码加密器
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String rawPassword = "123456";
        
        // 生成加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后密码: " + encodedPassword);
        
        // 验证密码
        String initSqlPassword = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2";
        boolean matches = passwordEncoder.matches(rawPassword, initSqlPassword);
        System.out.println("与init.sql中的密码匹配: " + matches);
        
        // 生成多个密码哈希，以便选择一个替换到init.sql中
        System.out.println("\n生成多个密码哈希:");
        for (int i = 0; i < 3; i++) {
            String hash = passwordEncoder.encode(rawPassword);
            System.out.println("哈希 " + (i + 1) + ": " + hash);
        }
    }
}