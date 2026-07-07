package com.old.silence.auth.center.util;

import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author moryzang
 */
@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 生成随机密码（如果需要系统生成密码）
     */
    public String generateRandomPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*";
        String allChars = upperCase + lowerCase + numbers + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);

        // 确保密码包含各种字符类型
        sb.append(upperCase.charAt(random.nextInt(upperCase.length())));
        sb.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        sb.append(numbers.charAt(random.nextInt(numbers.length())));
        sb.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 填充剩余长度
        for (int i = 4; i < 12; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 打乱顺序
        char[] passwordArray = sb.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
}