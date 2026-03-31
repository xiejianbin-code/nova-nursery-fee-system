package com.nova.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码生成器
 * 用于生成BCrypt加密的密码
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        // 创建密码加密器
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 生成密码
        String password = "123456";
        String encodedPassword = passwordEncoder.encode(password);
        
        System.out.println("密码: " + password);
        System.out.println("加密后: " + encodedPassword);
    }
}