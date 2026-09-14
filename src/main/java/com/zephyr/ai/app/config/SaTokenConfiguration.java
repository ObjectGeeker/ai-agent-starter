package com.zephyr.ai.app.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 与 JWT 集成配置（Simple 简单模式）。
 * <p>
 * Simple 模式仅将 Token 风格替换为 JWT，登录数据仍存储于 Redis，
 * 支持踢人/顶人下线、会话管理、id 反查 token 等完整能力，
 * 是与 Redis 持久化兼容性最好的模式。
 */
@Configuration
public class SaTokenConfiguration {

    /**
     * 注入 JWT 风格的 {@link StpLogic} 实现（Simple 模式）。
     *
     * @return JWT 简单模式的 StpLogic 实现
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
