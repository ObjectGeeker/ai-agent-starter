package com.zephyr.ai.domain.user.service;

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Service;

/**
 * 用户密码处理服务。
 * <p>
 * 使用 Hutool BCrypt 生成和校验密码哈希，不保存明文密码。
 */
@Service
public class UserPasswordService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 56;

    /**
     * 生成密码哈希。
     *
     * @param rawPassword 明文密码，仅在内存中短暂存在
     * @return BCrypt 密码哈希
     */
    public String hash(String rawPassword) {
        validate(rawPassword);
        return BCrypt.hashpw(rawPassword);
    }

    /**
     * 校验明文密码是否匹配已保存的哈希。
     *
     * @param rawPassword 明文密码
     * @param passwordHash 已保存的 BCrypt 哈希
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String passwordHash) {
        if (rawPassword == null || rawPassword.isBlank() || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, passwordHash);
    }

    private void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (rawPassword.length() < MIN_PASSWORD_LENGTH || rawPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("密码长度必须为 8 到 56 个字符");
        }
    }

}
